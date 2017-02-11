package com.lefu.es.blenew.helper;

import android.bluetooth.BluetoothGattCharacteristic;
import android.text.TextUtils;

import com.holtek.libHTBodyfat.HTBodyfatGeneral;
import com.holtek.libHTBodyfat.HTDataType;
import com.lefu.es.blenew.bean.Records1;
import com.lefu.es.blenew.service.BluetoothLeService1;
import com.lefu.es.blenew.utils.StringUtils1;
import com.lefu.es.blenew.utils.UtilTooth1;

/**
 * 作者: andy on 2016/11/24.
 * 作用: BLE工具类
 */

public class BleHelper1 {
    private BleHelper1(){}
    private static BleHelper1 bleHelper = null;

    public static synchronized BleHelper1 getInstance() {
        if (bleHelper == null) {
            bleHelper = new BleHelper1();
        }
        return bleHelper;
    }

    /**
     * 开启监听阿里通道
     * @param mBluetoothLeService
     */
    public  void listenAliScale(BluetoothLeService1 mBluetoothLeService){
        if(null!=mBluetoothLeService){
            // 监听 阿里秤 读通道
            final BluetoothGattCharacteristic characteristic = mBluetoothLeService.getCharacteristicNew(mBluetoothLeService.getSupportedGattServices(), "2a9c");
            mBluetoothLeService.setCharacteristicIndaicate(characteristic, true); // 开始监听通道
        }
    }

    /**
     * 构建阿里秤 发送数据
     * @param unit  单位  00, 01, 02
     * @param group 用户组 00,01,02,03....
     */
    public String assemblyAliData(String unit,String group){
        // 获取 校验位
        String xor = Integer.toHexString(StringUtils1.hexToTen("fd") ^ StringUtils1.hexToTen("37")^ StringUtils1.hexToTen(unit) ^ StringUtils1.hexToTen(group));
        return "fd37"+unit + group + "000000000000" + xor;
    }


    /**
     * 解析称端数据
     * @param readMessage 称端发送上来数据
     * @param height  身高
     * @param sex      性别
     * @param age      年龄
     * @param level    级别
     * @return
     */
    public Records1 parseScaleData(String readMessage, double height, int sex, int age, int level){
        if(TextUtils.isEmpty(readMessage) || readMessage.length()<35){return null;}
        Records1 recod = new Records1();
        double weight = StringUtils1.hexToTen(readMessage.substring(24, 26)+readMessage.substring(22, 24))*0.01d;
        int impedance = StringUtils1.hexToTen(readMessage.substring(34, 36)+readMessage.substring(32, 34) + readMessage.substring(30, 32));
        HTBodyfatGeneral bodyfat = new HTBodyfatGeneral(weight,height,sex, age, level, impedance);
        if(bodyfat.getBodyfatParameters() == HTDataType.ErrorNone){
            //正常计算
            recod.setRbmi(UtilTooth1.keep1Point3(bodyfat.BMI*0.1f));
            recod.setRbmr((int)bodyfat.BMR);
            recod.setRbodyfat(UtilTooth1.keep1Point3(bodyfat.bodyfatPercentage));
            recod.setRbodywater(UtilTooth1.keep1Point3(bodyfat.waterPercentage));
            recod.setRbone(UtilTooth1.keep1Point3(bodyfat.boneKg));
            recod.setRmuscle(UtilTooth1.keep1Point3(bodyfat.muscleKg));
            recod.setRvisceralfat((int) bodyfat.VFAL);
            float bmi = UtilTooth1.countBMI2(recod.getRweight(), (float) (height / 100));
            recod.setBodyAge(UtilTooth1.getPhysicAge(bmi,age));
            recod.setEffective(true);
        }else {
            recod.setEffective(false);
            recod.setRbmi(UtilTooth1.countBMI2(recod.getRweight(), (float) (height / 100)));
        }
        return recod;
    }


    /**
     * 发送 数据给称
     * @param mBluetoothLeService  fd37000999000000000000953
     * @param data
     */
    public void sendDateToScale(BluetoothLeService1 mBluetoothLeService, String data) {
        if(TextUtils.isEmpty(data) || null==mBluetoothLeService) return;
        // 通知秤
        final BluetoothGattCharacteristic characteristic2 = mBluetoothLeService.getCharacteristic(mBluetoothLeService.getSupportedGattServices(), "fff4");
        if (characteristic2 != null) {
            mBluetoothLeService.setCharacteristicNotification(characteristic2, true);
        }
        try {
            Thread.sleep(400);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 获取秤写通道
        final BluetoothGattCharacteristic characteristic = mBluetoothLeService.getCharacteristic(mBluetoothLeService.getSupportedGattServices(), "fff1");
        if (characteristic != null) {
            final byte[] dataArray = StringUtils1.hexStringToByteArray(data);
            characteristic.setValue(dataArray);
            mBluetoothLeService.wirteCharacteristic(characteristic);// 发送数据
            characteristic.getProperties();
        }
        try {
            Thread.sleep(500);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 获取秤写通道
        //final BluetoothGattCharacteristic characteristic = mBluetoothLeService.getCharacteristic(mBluetoothLeService.getSupportedGattServices(), "fff1");
        if (characteristic != null) {
            final byte[] dataArray = StringUtils1.hexStringToByteArray(data);
            characteristic.setValue(dataArray);
            mBluetoothLeService.wirteCharacteristic(characteristic);// 发送数据
            characteristic.getProperties();
        }
    }
}
