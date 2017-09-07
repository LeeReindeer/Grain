package xyz.leezoom.grain.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TcpCommon {
    private static int FileLength = 0;
    private static int _blockLength = 32768;

    public String ReceiveString(InputStream inS) {
        int index = 0;
        int count = (int) GetSize(inS);
        FileLength = count;
        if (count <= 0) {
            return "";
        }
        byte[] resultbyte = new byte[count];
        byte[] clientData = new byte[_blockLength];
        while (index < count) {
            try {
                int receivedBytesLen = inS.read(clientData);
                if (receivedBytesLen == -1) {
                    break;
                }
                System.arraycopy(clientData, 0, resultbyte, index, receivedBytesLen);
                index += receivedBytesLen;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new String(resultbyte, 0, index);
    }

    public int SendString(OutputStream outS, String sendString) {
        return SendBytes(outS, sendString.getBytes());
    }

    public int SendBytes(OutputStream outS, byte[] sendBytes) {
        try {
            int count = sendBytes.length;
            SendSize(outS, count);
            int index = 0;
            while (index < count) {
                int currentBlockLength;
                if (_blockLength > count - index) {
                    if (count - index <= 0) {
                        break;
                    }
                    currentBlockLength = count - index;
                } else {
                    currentBlockLength = _blockLength;
                }
                if (index + currentBlockLength > count) {
                    break;
                }
                outS.write(sendBytes, index, currentBlockLength);
                index += currentBlockLength;
            }
            outS.flush();
            return index;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public byte[] ReadBytes(InputStream inS) {
        try {
            int count = (int) GetSize(inS);
            FileLength = count;
            if (count == 0) {
                return new byte[0];
            }
            byte[] buffer = new byte[count];
            int index = 0;
            try {
                byte[] bufferBytes = new byte[_blockLength];
                while (index < count) {
                    try {
                        int receivedBytesLen = inS.read(bufferBytes);
                        if (receivedBytesLen == -1) {
                            break;
                        }
                        System.arraycopy(bufferBytes, 0, buffer, index, receivedBytesLen);
                        index += receivedBytesLen;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return buffer;
            } catch (Exception e2) {
                return new byte[0];
            }
        } catch (Exception e3) {
            return new byte[0];
        }
    }

    public int SendFile(OutputStream outS, String sendFile) {
        int sendbytes = 0;
        byte[] buffer = new byte[_blockLength];
        try {
            BufferedInputStream fi = new BufferedInputStream(new FileInputStream(new File(sendFile)));
            SendSize(outS, fi.available());
            while (true) {
                int len = fi.read(buffer);
                if (len <= 0) {
                    break;
                }
                outS.write(buffer, 0, len);
                outS.flush();
                sendbytes += len;
            }
            fi.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sendbytes;
    }

    public static void DeleteFile(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }

    public static boolean ExistFile(String filename) {
        if (new File(filename).exists()) {
            return true;
        }
        return false;
    }

    public int ReceiveFile(InputStream inS, String receiveFile) {
        int readlens = 0;
        byte[] buffer = new byte[_blockLength];
        try {
            int count = (int) GetSize(inS);
            FileLength = count;
            if (count <= 0) {
                return 0;
            }
            DeleteFile(receiveFile);
            BufferedOutputStream fo = new BufferedOutputStream(new FileOutputStream(new File(receiveFile)));
            while (readlens < count) {
                try {
                    int bytesRead = inS.read(buffer);
                    if (bytesRead == -1) {
                        break;
                    }
                    fo.write(buffer, 0, bytesRead);
                    readlens += bytesRead;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            fo.flush();
            fo.close();
            return readlens;
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return readlens;
    }

    private void SendSize(OutputStream outS, int length) {
        byte[] filelens = longToByteArray((long) length);
        try {
            outS.write(filelens, 0, filelens.length);
            outS.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long GetSize(InputStream stream) {
        long count = 0;
        byte[] countBytes = new byte[8];
        try {
            if (stream.read(countBytes) != 8) {
                return 0;
            }
            count = ByteArrayTolong(countBytes);
            return count;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    public int GetLength() {
        return FileLength;
    }

    public byte[] longToByteArray(long s) {
        byte[] targets = new byte[8];
        for (int i = 0; i < 8; i++) {
            targets[i] = (byte) ((int) ((s >> ((7 - i) * 8)) & 255));
        }
        return targets;
    }

    public long ByteArrayTolong(byte[] res) {
        long targets = 0;
        for (int i = 0; i < 8; i++) {
            targets += ((long) (res[i] & 255)) << ((7 - i) * 8);
        }
        return targets;
    }
}

