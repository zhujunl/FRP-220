# FPR-220

## Demo：V1.0.3｜SDK：V2.0.4.20221017 

SDK源代码：http://192.168.11.216/androidsdk/uart/fpr220comdriver/-/tree/dev_fpr-220

提测源代码：[https://devops03.miaxishz.com/svn/基线源代码/产品开发中心/指纹类/Android/FPR-220 V3.0/FPR-220_Demo V1.0.3.20221017.zip](https://devops03.miaxishz.com/svn/基线源代码/产品开发中心/指纹类/Android/FPR-220 V3.0)

提测包：[https://devops03.miaxishz.com/svn/测试库/产品开发中心/SDK/Android/FPR-220 V3.0/Android FPR-220 UART ISO SDK-V1.0.3.20221017.zip](https://devops03.miaxishz.com/svn/测试库/产品开发中心/SDK/Android/FPR-220 V3.0)

 **2022年10月17**

1. 修复波特率小于115200的时候的传图失败；
2. 下位机容量直接开放到1000;
3. 下位机超时时间默认改成3000ms;
4. 下位机的特征是1000个的时候，搜索大概5.2s，超时时间改成3*5s;

## Demo：V1.0.2｜SDK：V2.0.3.20221010 

SDK源代码：http://192.168.11.216/androidsdk/uart/fpr220comdriver/-/tree/dev_fpr-220

提测源代码：[https://devops03.miaxishz.com/svn/基线源代码/产品开发中心/指纹类/Android/FPR-220 V3.0/FPR-220_Demo V1.0.2.20221010.zip](https://devops03.miaxishz.com/svn/基线源代码/产品开发中心/指纹类/Android/FPR-220 V3.0)

提测包：[https://devops03.miaxishz.com/svn/测试库/产品开发中心/SDK/Android/FPR-220 V3.0/Android FPR-220 UART ISO SDK-V1.0.2.20221010.zip](https://devops03.miaxishz.com/svn/测试库/产品开发中心/SDK/Android/FPR-220 V3.0)

 **2022年10月10**

1. 基础功能模块：指纹照片显示、日志显示、手指检测、采图、Video;
1. 设置模块：活体、NFIQ、波特率设置;
1. 上位机模块：算法选择、注册、比对、搜索、删除、清空、数据库展示;
1. 下位机模块：算法选择、注册、比对、搜索、删除、清空、上传特征、存储容量;
1. 导出模块：不同特征导出、指纹照片导出、FIR导出;
1. 信息模块：指纹NFIQ、指纹特征展示、sdk版本、算法版本、下位机版本、活体算法版本
