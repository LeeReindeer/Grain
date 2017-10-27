/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 10/27/17 3:54 PM
 */

package xyz.leezoom.grain.module;

/**
 * @Author lee
 * @Time 9/4/17.
 */
public enum QueryType {

    ZFQueryXueshengChengji, //query mark
    ZFQueryCengkeToday, //query today's class
    ZFQueryCengkeXYMC, //query faculty
    ZFAllClassnames, //query all class name

    Validation, //token
    VersionNumber, //get  last version number
    Version,

    CardUserBaseInfo, //user's card base info
    CardUserPicture, //user's pic
    CardUserPayment, //user's payment

    TsgQueryBooks, //query book
    TsgUserLibrarys
}
