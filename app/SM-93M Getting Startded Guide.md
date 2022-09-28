# SM-93M Getting Startded Guide

[TOC]

## 1.Introduction

Welcome to use SM-93M SDK. This document will introduce how to use SM-93M SDK for development in Android.



## 2.SDK Contents

The SDK contains libraries and demo needed for the development of SM-93M,Contains the following directories:

- /apk or /bin

  The `apk` or `bin` folder contains the compiled binary files, which can be installed directly on the Android device.

- /demo 

  The `demo` folder is the source code corresponding to the apk file, which can be opened using Android Studio . 

> NOTE:
>
> The library (.so & .jar & .aar) file is included in the demo and is not provided separately.



## 3.Development Guide

### 3.1 Configure your project

This lesson shows you how to create a new Android project using Android Studio and introduces some of the files in the project.

To create a new Android project, follow these steps:

#### 3.1.1.  Create a new Android project

Create a new Empty Activity application project,You can refer to [Android Developer](https://developer.android.com/) to create an Android project.

#### 3.1.2. Enable Java 8

The SDK needs to work with **JDK 1.8**, open **build.gradle**, add a configuration inside, as shown in the figure:

![enable java 8](./images/java8.png)



#### 3.1.3. Add aar file

First, switch to the project view. Click on `Android` and select `Project`.As shown below:

![Switch to project view](./images/project.png)

Then, copy the aar from the demo project to the lib directory of your project, as shown below:

![Copy jar](./images/libs.png)

> NOTE:
>
> `JustouchApi.aar` is used for fingerprint algorithm.
>
> `SM93MDriverApi.aar` is used to access `SM-93M` Device.

#### 3.1.4. Congratulations

Congratulations, you have completed all the preparations, then you can refer to the **Sample Code** to learn how to use our API.

### 3.2 Sample Code

#### 3.2.1 SM-93M Driver API

Get a instance of SM-93M driver API :
```java
SM93MApi mDriverApi = SM93MApiFactory.getInstance(getApplicationContext());
// do something with mDriverApi
```

Open/Close SM-93M device :

```java
int fd = mDriverApi.openDevice();
if (fd >= 0) {
  // Open successfully !
  // 0 means it has been opened before
} else {
  // process error code 
}
```

Capture fingerprint image from SM-93M device:

```java
CaptureConfig captureConfig = new CaptureConfig.Builder()
                .setLfdLevel(0)
                .setLatentLevel(0)
                .setTimeout(8000)
                .setAreaScore(45)
                .setPreviewCallBack(previewCallBack)
                // AES/ECB/PKCS5Padding
                //.setAESConfig(new AESConfig.Builder().setKey("1234567890123456").build())
                //.setAESStatus(CaptureConfig.AES_HOST)
                .build();
MxResult<MxImage> image = mDriverApi.getImage(captureConfig);
if (imageResult.isSuccess()) {
  // Capture successfully !
  MxImage mxImage = imageResult.data;
  // Convert raw image to Android bitmap
  byte[] imageDate = new byte[mxImage.width * mxImage.height + 1078];
  BmpLoader.Raw2Bmp(imageDate, mxImage.data, mxImage.width, mxImage.height);
  Bitmap bitmap = BitmapFactory.decodeByteArray(imageDate, 0, imageDate.length);
  //show bitmap
  ImageView imageView ;//The imageView to show fingerprint image
  imageView.setImageBitmap(bitmap);
} else {
  //process error code 
}
```

#### 3.2.2 Justouch Api

Create a instance of Jutouch Api :

```java
JustouchFingerAPI mJustouchApi = new JustouchFingerAPI();
```

Create FMR : 

```java
MxImage image ; // capture from SM-93M
byte[] fmrBuffer = new byte[1024];//Must be 1024 bytes
int result = mJustouchApi.createTemplateISO(image.data, image.width, image.height, fmrBuffer);
if (result >= 0) {
  // successfully
}
// If you use ISO2011, please call the following function :
// mJustouchApi.createTemplateISO2011(...)
// If you use ANSI, please call the following function :
// mJustouchApi.createTemplateANSI(...)
```

Match two FMR : 

```java
byte[] fmrBufferA //Must be 1024 bytes
byte[] fmrBufferB //Must be 1024 bytes
int similarScore = mJustouchApi.compareTemplatesANSI(fmrBufferA, fmrBufferB);
if (score >= 45) { // Suggest 45 pass
  // Match passed
} else if (score >= 0) {
  // Match not passed
} else {
  // Process error code 
}

```

> NOTE: 
>
> When Justouch works with SM-93M, there is no need to call initialization



## 4. Support Contact Information:

MIAXIS BIOMETRICS CO., LTD



**Official website:**

www.miaxis.net , www.miaxis.com

**Sales :**

amy@miaxis.com

**Technical Support :**

developer@miaxis.com

