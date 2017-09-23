package xyz.leezoom.grain.util;

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

