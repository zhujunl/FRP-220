package com.miaxis.sm93m;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.miaxis.fingerprint.ISOANSI;
import com.miaxis.justouch.JustouchFingerAPI;

import org.zz.jni.FingerLiveApi;
import org.zz.jni.mxComFingerDriver;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainViewModel extends ViewModel {
    private static final String TAG = "MainViewModel";

    public MainViewModel() {
        Log.d(TAG, "MainViewModel init");
        communicationTypeRadio.setValue(R.id.rb_uart);
        imageSizeRadio.setValue(R.id.rb_uncompressed);
    }

    private Context mContext;
    private mxComFingerDriver mFingerDriverApi;
    private JustouchFingerAPI mJustouchApi;
    private final DiskTemplates mDiskTemplates = new DiskTemplates();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    //Communication type [0: usb virtual serial port] [1: serial port]
    public MutableLiveData<Integer> communicationType = new MutableLiveData<>(1);
    public MutableLiveData<Integer> communicationTypeRadio = new MutableLiveData<>();

    public MutableLiveData<Integer> imageSizeRadio = new MutableLiveData<>();

    public MutableLiveData<Boolean> opened = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> busy = new MutableLiveData<>();
    public MutableLiveData<String> log = new MutableLiveData<>();
    public MutableLiveData<Bitmap> bm = new MutableLiveData<>();
    public MutableLiveData<Boolean> video = new MutableLiveData<>();
    public MutableLiveData<Integer> featureType = new MutableLiveData<>(1);
    private String hostFeatureNameNow = "";
    public MutableLiveData<Integer> hostFeatureType = new MutableLiveData<>(1);
    public MutableLiveData<Integer> bufferId = new MutableLiveData<>(-1);
    public MutableLiveData<String> hostUserId = new MutableLiveData<>();
    public MutableLiveData<Boolean> encrypted = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> latent = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> lfd = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> nfiq = new MutableLiveData<>(false);
    public MutableLiveData<Integer> nfiqLevel = new MutableLiveData<>(2);
    public MutableLiveData<String> comPath = new MutableLiveData<>("/dev/ttyHSL2");
    public MutableLiveData<Integer> baudRate = new MutableLiveData<>(3);
    public int[] baudRateValue = {115200, 230400, 460800, 921600};
    private int baudRateOld = 0;
    public MutableLiveData<Boolean> firstInit = new MutableLiveData<>(true);
    private MxImage templateMxImage;
    private int capacity = 1000;

    /**
     * Minimum acceptable number of fingerprint minutiae points
     */
    private static final int MIN_MINUTIAE_COUNT = 12;

    public void setFingerDriverApi(mxComFingerDriver fingerDriverApi) {
        mFingerDriverApi = fingerDriverApi;
    }

    public JustouchFingerAPI getJustouchApi() {
        return mJustouchApi;
    }

    public void setJustouchApi(JustouchFingerAPI justouchApi) {
        mJustouchApi = justouchApi;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void checkConnected() {
        log.setValue(null);
        busy.setValue(true);
        executor.execute(() -> {
            byte[] bytes = new byte[100];
            String path = comPath.getValue();
            if (baudRate.getValue() == null) return;
            int rate = baudRateValue[baudRate.getValue()];
            int result = mFingerDriverApi.mxGetComDevVersion(path, rate, bytes);
            if (result == 0) {
                log.postValue("[CHECK CONNECTED]\nSuccess\nDeviceVersion: " + new String(bytes));
                baudRateOld = rate;
                opened.postValue(true);
            } else {
                if (result == 0x54){
                    log.postValue("[CHECK CONNECTED]\nFailed\nTimeout");
                } else {
                    log.postValue("[CHECK CONNECTED]\nFailed\n" + result);
                }
            }
            busy.postValue(false);
        });
    }

    public void setBaudRate() {
        log.setValue(null);
        busy.setValue(true);
        executor.execute(() -> {
            String path = comPath.getValue();
            if (baudRate.getValue() == null) return;
            int rate = baudRateValue[baudRate.getValue()];
            int result = mFingerDriverApi.mxSetFingerBaudRate(path, baudRateOld, 3000, baudRate.getValue() + 1 + 4);
            if (result != 0) {
                if (result == -3){
                    log.postValue("[SET BAUD RATE]\nFailed\nTimeout");
                } else {
                    log.postValue("[SET BAUD RATE]\nFailed\nFAIL Code: " + result);
                }
            } else {
                baudRateOld = rate;
                log.postValue("[SET BAUD RATE]\nSuccess");
            }
            busy.postValue(false);
        });
    }

    public void getAlgorithmVersion() {
        log.setValue("[ALGORITHM VERSION]\nSuccess\n" + mJustouchApi.getFpAlgVersion());
    }

    public void getSDKVersion() {
        byte[] bytes = new byte[100];
        mFingerDriverApi.mxGetComDriverVersion(bytes);
        log.postValue("[SDK VERSION]\nSuccess\n" + new String(bytes));
    }

    public void getLiveAlgVersion() {
        log.setValue("[FINGER LIVE ALG VERSION]\nSuccess\n" + FingerLiveApi.getAlgVersion());
    }

    public void getDeviceInfo() {
        log.setValue(null);
        executor.execute(() -> {
            byte[] bytes = new byte[100];
            String path = comPath.getValue();
            if (baudRate.getValue() == null) return;
            int rate = baudRateValue[baudRate.getValue()];
            int result = mFingerDriverApi.mxGetComDevVersion(path, rate, bytes);
            if (result == 0) {
                log.postValue("[DEVICE VERSION]\nSuccess\n" + new String(bytes));
            } else {
                log.postValue("[DEVICE VERSION]\nFailed\n" + result);
            }
        });
    }

    public void getCapacity() {
        log.setValue(null);
        executor.execute(() -> {
            String path = comPath.getValue();
            if (baudRate.getValue() == null) return;
            int rate = baudRateValue[baudRate.getValue()];
            short[] capacity = {0};
            int result = mFingerDriverApi.mxGetDevTzCapacity(path, rate, capacity);
            if (result == 0) {
                this.capacity = capacity[0];
                log.postValue("[CAPACITY]\nSuccess\n" + capacity[0]);
            } else {
                log.postValue("[CAPACITY]\nFailed\n\n" + result);
            }
        });
    }

    private MxImage getImage() {
        String path = comPath.getValue();
        if (baudRate.getValue() == null || imageSizeRadio.getValue() == null)
            return new MxImage(-1, 0, 0, 0, null);
        int rate = baudRateValue[baudRate.getValue()];
        if (imageSizeRadio.getValue() == R.id.rb_compressed) {
            byte[] image = new byte[128 * 180];
            int result = mFingerDriverApi.mxGetFingerImageWithCompression(path, rate, 5000, image);
            if (result == 0) {
                MxImage mxImage = new MxImage(0, 128, 180, 1, image);
                return mxImage;
            } else {
                return new MxImage(result, 0, 0, 0, null);
            }
        } else {
            byte[] image = new byte[256 * 360];
            Log.d(TAG, "rate is " + rate);
            int result = mFingerDriverApi.mxGetFingerImage(path, rate, 5000, image);
            if (result == 0) {
                MxImage mxImage = new MxImage(0, 256, 360, 1, image);
                return mxImage;
            } else {
                return new MxImage(result, 0, 0, 0, null);
            }
        }
    }

    public void getFinalImage() {
        log.setValue(null);
        busy.setValue(true);
        bm.setValue(null);
        executor.execute(() -> {
            templateMxImage = null;
            long startTime = System.currentTimeMillis();
            MxImage result = getImage();
            long endTime = System.currentTimeMillis();
            if (result.error == 0) {
                if (nfiq.getValue() != null && nfiq.getValue() && nfiqLevel.getValue() != null) {
                    int nfiq = mJustouchApi.getNFIQ(result.data, result.width, result.height);
                    if (nfiq < 0) {
                        log.postValue("[CAPTURE]\nNFIQ Failed\nFAIL Code: " + nfiq);
                        busy.postValue(false);
                        return;
                    }
                    if (nfiq > nfiqLevel.getValue()) {
                        log.postValue("[CAPTURE]\nFailed\nNFIQ reject");
                        busy.postValue(false);
                        return;
                    }
                }
                if (lfd.getValue() != null && lfd.getValue()) {
                    int[] lfdResult = {0};
                    int i = FingerLiveApi.fingerLiveWithLevel(result.data, result.width, result.height, 3, lfdResult);
                    if (i != 0) {
                        log.postValue("[CAPTURE]\nLFD Failed\nFAIL Code: " + i);
                        busy.postValue(false);
                        return;
                    }
                    if (lfdResult[0] == 0) {
                        log.postValue("[CAPTURE]\nFailed\nLFD reject");
                        busy.postValue(false);
                        return;
                    }
                }
                showFingerImage(result);
                log.postValue("[CAPTURE]\nSuccess\nTime: " + (endTime - startTime) + "ms");
            } else {
                if (result.error == -3) {
                    log.postValue("[VIDEO]\nFailed\nTimeout");
                } else {
                    log.postValue("[CAPTURE]\nFailed\nFAIL Code: " + result.error);
                }
            }
            busy.postValue(false);
        });
    }

    public void viewOn() {
        if (video.getValue() == null || !video.getValue()) {
            video.setValue(true);
            executor.execute(() -> {
                busy.postValue(true);
                log.postValue(null);
                bm.postValue(null);
                templateMxImage = null;
                while (video.getValue()) {
                    long startTime = System.currentTimeMillis();
                    MxImage result = getImage();
                    long endTime = System.currentTimeMillis();
                    if (result.error != 0) {
                        if (result.error == -3 || result.error == -6) {
                            showFingerImage(null);
                            log.postValue("[VIDEO]\nImage Failed\nno finger");
                            continue;
                        } else {
                            log.postValue("[VIDEO]\nImage Failed\nFAIL Code: " + result.error);
                            video.postValue(false);
                            break;
                        }
                    }
                    showFingerImage(result);
                    long nfiqTime = 0;
                    if (nfiq.getValue() != null && nfiq.getValue() && nfiqLevel.getValue() != null) {
                        int nfiq = mJustouchApi.getNFIQ(result.data, result.width, result.height);
                        nfiqTime = System.currentTimeMillis();
                        if (nfiq < 0) {
                            log.postValue("[VIDEO]\nNFIQ Failed\nFAIL Code: " + nfiq);
                            continue;
                        }
                        if (nfiq > nfiqLevel.getValue()) {
                            log.postValue("[VIDEO]\nFailed\nNFIQ reject");
                            continue;
                        }
                    }
                    long lfdStartTime = 0;
                    long lfdEndTime = 0;
                    if (lfd.getValue() != null && lfd.getValue()) {
                        lfdStartTime = System.currentTimeMillis();
                        int[] lfdResult = {0};
                        int i = FingerLiveApi.fingerLiveWithLevel(result.data, result.width, result.height, 3, lfdResult);
                        lfdEndTime = System.currentTimeMillis();
                        if (i != 0) {
                            log.postValue("[VIDEO]\nLFD Failed\nFAIL Code: " + i);
                            busy.postValue(false);
                            continue;
                        }
                        if (lfdResult[0] == 0) {
                            log.postValue("[VIDEO]\nFailed\nLFD reject");
                            busy.postValue(false);
                            continue;
                        }
                    }
                    StringBuilder msg = new StringBuilder();
                    msg.append("[VIDEO]\nSuccess\nImage Time: ").append(endTime - startTime).append("ms");
                    if (nfiqTime != 0) {
                        msg.append("\n").append("NFIQ Time: ").append(nfiqTime - endTime).append("ms");
                    }
                    if (lfdStartTime != 0) {
                        msg.append("\n").append("LFD Time: ").append(lfdEndTime - lfdStartTime).append("ms");
                    }
                    log.postValue(msg.toString());
                }
                busy.postValue(false);
            });
        }
    }

    public void viewOff() {
        if (video.getValue() != null && video.getValue()) {
            video.postValue(false);
        }
    }

    private void showFingerImage(MxImage mxImage) {
        if (mxImage == null) {
            bm.postValue(null);
            templateMxImage = null;
        } else {
            templateMxImage = mxImage;
            byte[] imageDate = new byte[mxImage.width * mxImage.height + 1078];
            int raw2Bmp = mJustouchApi.convertRawToBMP(mxImage.data, mxImage.width, mxImage.height, imageDate);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageDate, 0, imageDate.length);
            bm.postValue(bitmap);
        }
    }

    public void detectFinger() {
        log.setValue(null);
        executor.execute(() -> {
            String path = comPath.getValue();
            if (baudRate.getValue() == null) return;
            int rate = baudRateValue[baudRate.getValue()];
            int[] area = {0};
            int result = mFingerDriverApi.mxGetImageAndArea(path, rate, area);
            if (result == 0) {
                if (area[0] > 0) {
                    log.postValue("[DETECT FINGER]\nSuccess\nhas finger\nfinger area: " + area[0] + "%");
                } else {
                    log.postValue("[DETECT FINGER]\nSuccess\nno finger");
                }
            } else {
                log.postValue("[DETECT FINGER]\nFailed\nFAIL Code: " + result);
            }
        });
    }

    public int genImage(String path, int rate) {
        long totalTime = System.currentTimeMillis() + 8000;
        while (System.currentTimeMillis() < totalTime) {
            int[] area = {0};
            int i = mFingerDriverApi.mxGetImageAndArea(path, rate, area);
            if (i != 0) {
                return i;
            }
            if (area[0] > 45) {
                return 0;
            }
        }
        return -1;
    }

    public void enroll() {
        if (bufferId.getValue() == null || bufferId.getValue() < 0) {
            log.setValue("[ENROLL]\nFail\nPlease input buffer id");
            busy.setValue(false);
            return;
        }
        busy.setValue(true);
        bm.setValue(null);
        templateMxImage = null;
        executor.execute(() -> {
            long st = System.currentTimeMillis();
            String path = comPath.getValue();
            if (baudRate.getValue() == null) return;
            int rate = baudRateValue[baudRate.getValue()];
            if (bufferId.getValue() == null) return;
            int position = bufferId.getValue();
            if (position < 0) {
                log.postValue("[ENROLL]\nFail\nBufferId must be greater than 0");
                busy.postValue(false);
                return;
            }
            if (position >= capacity) {
                log.postValue("[ENROLL]\nFail\nBufferId must be less than " + capacity);
                busy.postValue(false);
                return;
            }
            int genImage = genImage(path, rate);
            if (genImage == 0) {
                int i1 = mFingerDriverApi.mxGenTz(path, rate, (short) 1, (short) position);
                if (i1 == 0) {
                    log.postValue("[ENROLL]\nSuccess\nTime: " + (System.currentTimeMillis() - st) + "ms");
                } else {
                    log.postValue("[ENROLL]\nFail\nFAIL Code: " + i1);
                }
            } else {
                log.postValue("[ENROLL]\nFail\nFAIL Code: " + genImage);
            }
            busy.postValue(false);
        });
    }

    public void verify() {
        if (bufferId.getValue() == null || bufferId.getValue() < 0) {
            log.setValue("[VERIFY]\nFail\nPlease input buffer id");
            busy.setValue(false);
            return;
        }
        busy.setValue(true);
        bm.setValue(null);
        templateMxImage = null;
        executor.execute(() -> {
            long st = System.currentTimeMillis();
            String path = comPath.getValue();
            if (baudRate.getValue() == null) return;
            int rate = baudRateValue[baudRate.getValue()];
            if (bufferId.getValue() == null) return;
            int position = bufferId.getValue();
            if (position < 0) {
                log.postValue("[VERIFY]\nFail\nBufferId must be greater than 0");
                busy.postValue(false);
                return;
            }
            if (position >= capacity) {
                log.postValue("[VERIFY]\nFail\nBufferId must be less than " + capacity);
                busy.postValue(false);
                return;
            }
            int genImage = genImage(path, rate);
            if (genImage == 0) {
                int i1 = mFingerDriverApi.mxGenTz(path, rate, (short) 0, (short) 0);
                if (i1 == 0) {
                    int[] matchResult = {0};
                    int i2 = mFingerDriverApi.mxMatch(path, rate, (short) 0, (short) 0, (short) 1, (short) position, matchResult);
                    if (i2 == 0) {
                        if (matchResult[0] == 1) {
                            log.postValue("[VERIFY]\nSuccess\nPass\nTime: " + (System.currentTimeMillis() - st) + "ms");
                        } else {
                            log.postValue("[VERIFY]\nSuccess\nNot Pass\nTime: " + (System.currentTimeMillis() - st) + "ms");
                        }
                    } else {
                        log.postValue("[VERIFY]\nFail\nFAIL Code: " + i2);
                    }
                } else {
                    log.postValue("[VERIFY]\nFail\nFAIL Code: " + i1);
                }
            } else {
                log.postValue("[VERIFY]\nFail\nFAIL Code: " + genImage);
            }
            busy.postValue(false);
        });
    }

    public void search() {
        busy.setValue(true);
        bm.setValue(null);
        templateMxImage = null;
        executor.execute(() -> {
            long st = System.currentTimeMillis();
            String path = comPath.getValue();
            if (baudRate.getValue() == null) return;
            int rate = baudRateValue[baudRate.getValue()];
            int genImage = genImage(path, rate);
            if (genImage == 0) {
                int i1 = mFingerDriverApi.mxGenTz(path, rate, (short) 0, (short) 0);
                if (i1 == 0) {
                    short[] searchResult = {0};
                    int i2 = mFingerDriverApi.mxSearch(path, rate, (short) 0, (short) 0, (short) (capacity), searchResult);
                    if (i2 == 0) {
                        log.postValue("[SEARCH]\nSuccess\nBufferId: " + searchResult[0] + "\nTime: " + (System.currentTimeMillis() - st) + "ms");
                    } else {
                        log.postValue("[SEARCH]\nFail\nFAIL Code: " + i2);
                    }
                } else {
                    log.postValue("[SEARCH]\nFail\nFAIL Code: " + i1);
                }
            } else {
                log.postValue("[SEARCH]\nFail\nFAIL Code: " + genImage);
            }
            busy.postValue(false);
        });
    }

    public void delete() {
        if (bufferId.getValue() == null || bufferId.getValue() < 0) {
            log.setValue("[REMOVE]\nFail\nPlease input buffer id");
            busy.setValue(false);
            return;
        }
        executor.execute(() -> {
            String path = comPath.getValue();
            if (baudRate.getValue() == null) return;
            int rate = baudRateValue[baudRate.getValue()];
            int position = bufferId.getValue();
            if (position < 0) {
                log.postValue("[REMOVE]\nFail\nBufferId must be greater than 0");
                busy.postValue(false);
                return;
            }
            int result = mFingerDriverApi.mxDeleteTz(path, rate, (short) 1, (short) position);
            if (result == 0) {
                log.postValue("[REMOVE]\nSuccess");
            } else {
                log.postValue("[REMOVE]\nFail\nFAIL Code: " + result);
            }
        });
    }

    public void erase() {
        executor.execute(() -> {
            String path = comPath.getValue();
            if (baudRate.getValue() == null) return;
            int rate = baudRateValue[baudRate.getValue()];
            int result = mFingerDriverApi.mxClearTz(path, rate);

            if (result == 0) {
                log.postValue("[CLEAR]\nSuccess");
            } else {
                log.postValue("[CLEAR]\nFail\nFAIL Code: " + result);
            }
        });
    }

    public void upload() {
        if (bufferId.getValue() == null || bufferId.getValue() < 0) {
            log.setValue("[UPLOAD]\nFail\nPlease input buffer id");
            busy.setValue(false);
            return;
        }
        busy.setValue(true);
        executor.execute(() -> {
            String path = comPath.getValue();
            if (baudRate.getValue() == null) return;
            int rate = baudRateValue[baudRate.getValue()];
            int position = bufferId.getValue();
            if (position < 0) {
                log.postValue("[UPLOAD]\nFail\nBufferId must be greater than 0");
                busy.postValue(false);
                return;
            }
            if (position >= capacity) {
                log.postValue("[UPLOAD]\nFail\nBufferId must be less than " + capacity);
                busy.postValue(false);
                return;
            }
            byte[] templatesBytes = new byte[1024];
            short[] len = {(short) templatesBytes.length};
            int i2 = mFingerDriverApi.mxUpTz(path, rate, (short) 1, (short) position, templatesBytes, len);
            if (i2 == 0) {
                File file = new File(mContext.getExternalFilesDir(null), "MODULE");
                if (!file.exists()) file.mkdirs();
                boolean save = FileUtils.writeFeatureToFile(templatesBytes, file.getAbsolutePath(), "Type " + featureType.getValue() + " BufferId " + bufferId.getValue() + " feature.txt");
                log.postValue("[UPLOAD]\nSuccess\nSave Result: " + save + "\nTemplate Len: " + len[0]);
            } else {
                log.postValue("[UPLOAD]\nFail\nFAIL Code: " + i2);
            }
            busy.postValue(false);
        });
    }

    public void hostEnroll() {
        String userIdValue = hostUserId.getValue();
        if (TextUtils.isEmpty(userIdValue)) {
            log.postValue("[HOST ENROLL]\nFail\nPlease input your id");
            busy.postValue(false);
            return;
        }
        assert userIdValue != null;
        final String userId = userIdValue.trim();
        executor.execute(() -> {
            log.postValue("[HOST ENROLL]\nPlease wait...");
            busy.postValue(true);
            bm.postValue(null);
            templateMxImage = null;
            long startTime = System.currentTimeMillis();
            MxImage getImage = getImage();
            long getImageEndTime = System.currentTimeMillis();
            if (getImage.error != 0) {
                log.postValue("[HOST ENROLL]\nFailed\nFAIL Code: " + getImage.error);
                busy.postValue(false);
                return;
            }
            if (nfiq.getValue() != null && nfiq.getValue() && nfiqLevel.getValue() != null) {
                int nfiq = mJustouchApi.getNFIQ(getImage.data, getImage.width, getImage.height);
                if (nfiq < 0) {
                    log.postValue("[HOST ENROLL]\nNFIQ Failed\nFAIL Code: " + nfiq);
                    busy.postValue(false);
                    return;
                }
                if (nfiq > nfiqLevel.getValue()) {
                    log.postValue("[HOST ENROLL]\nFailed\nNFIQ reject");
                    busy.postValue(false);
                    return;
                }
            }
            if (lfd.getValue() != null && lfd.getValue()) {
                int[] lfdResult = {0};
                int i = FingerLiveApi.fingerLiveWithLevel(getImage.data, getImage.width, getImage.height, 3, lfdResult);
                if (i != 0) {
                    log.postValue("[HOST ENROLL]\nLFD Failed\nFAIL Code: " + i);
                    busy.postValue(false);
                    return;
                }
                if (lfdResult[0] == 0) {
                    log.postValue("[HOST ENROLL]\nFailed\nLFD reject");
                    busy.postValue(false);
                    return;
                }
            }
            showFingerImage(getImage);
            byte[] tempFeature = new byte[DiskTemplates.TEMPLATE_LENGTH];
            int[] templateLength = new int[1];
            int result = createTemplate(getImage.data, getImage.width, getImage.height, tempFeature, templateLength);
            if (result < 0) {
                log.postValue("[HOST ENROLL]\nFail\nGet new template failed!\nCode : " + result);
                busy.postValue(false);
                return;
            }
            int index = searchTemplates(tempFeature, mDiskTemplates.count(), mDiskTemplates.getAll());
            long timeEnd = System.currentTimeMillis();
            if (index >= 0) {
                String userIdTemp = mDiskTemplates.getId(index);
                log.postValue("[HOST ENROLL]\nFail\nThe fingerprint has been enrolled , Id is : " + userIdTemp);
                busy.postValue(false);
                return;
            }
            boolean addToDbSuccess = mDiskTemplates.put(userId, Arrays.copyOf(tempFeature, templateLength[0]));
            if (!addToDbSuccess) {
                log.postValue("[HOST ENROLL]\nFail\nThe id has been enrolled , Please re-enter id");
            } else {
                log.postValue("[HOST ENROLL]\nSuccess\nid : " + userId + "\nCapture Time : " + (getImageEndTime - startTime) + "ms\nEnroll Time : " + (timeEnd - getImageEndTime) + "ms");
            }

            busy.postValue(false);
        });
    }

    public void hostVerify() {
        String userIdValue = hostUserId.getValue();
        if (TextUtils.isEmpty(userIdValue)) {
            log.postValue("[HOST VERIFY]\nFail\nPlease input your id");
            return;
        }
        assert userIdValue != null;
        final String userId = userIdValue.trim();
        byte[] selectTemplate = mDiskTemplates.get(userId);
        if (selectTemplate == null) {
            log.postValue("[HOST VERIFY]\nFail\nReason : Not found id : " + userId);
            return;
        }
        executor.execute(() -> {
            if (bufferId.getValue() == null) bufferId.postValue(0);
            log.postValue("[HOST VERIFY]\nPlease wait...");
            busy.postValue(true);
            bm.postValue(null);
            templateMxImage = null;
            long start = System.currentTimeMillis();
            MxImage getImage = getImage();
            long getImageEndTime = System.currentTimeMillis();
            if (getImage.error != 0) {
                log.postValue("[HOST VERIFY]\nFail\nFAIL CODE: " + getImage.error);
                busy.postValue(false);
                return;
            }
            if (nfiq.getValue() != null && nfiq.getValue() && nfiqLevel.getValue() != null) {
                int nfiq = mJustouchApi.getNFIQ(getImage.data, getImage.width, getImage.height);
                if (nfiq < 0) {
                    log.postValue("[HOST VERIFY]\nNFIQ Failed\nFAIL Code: " + nfiq);
                    busy.postValue(false);
                    return;
                }
                if (nfiq > nfiqLevel.getValue()) {
                    log.postValue("[HOST VERIFY]\nFailed\nNFIQ reject");
                    busy.postValue(false);
                    return;
                }
            }
            if (lfd.getValue() != null && lfd.getValue()) {
                int[] lfdResult = {0};
                int i = FingerLiveApi.fingerLiveWithLevel(getImage.data, getImage.width, getImage.height, 3, lfdResult);
                if (i != 0) {
                    log.postValue("[HOST VERIFY]\nLFD Failed\nFAIL Code: " + i);
                    busy.postValue(false);
                    return;
                }
                if (lfdResult[0] == 0) {
                    log.postValue("[HOST VERIFY]\nFailed\nLFD reject");
                    busy.postValue(false);
                    return;
                }
            }
            showFingerImage(getImage);
            byte[] tempFeature = new byte[DiskTemplates.TEMPLATE_LENGTH];
            Log.d(TAG, "hostVerify createTemplate start");
            int result = createTemplate(getImage.data, getImage.width, getImage.height, tempFeature, null);
            Log.d(TAG, "hostVerify createTemplate end");
            if (result < 0) {
                log.postValue("[HOST VERIFY]\nFail\nGet new template failed!\nCode : " + result);
                busy.postValue(false);
                return;
            }
            Log.d(TAG, "hostVerify compareTemplates start");
            int score = compareTemplates(selectTemplate, tempFeature);
            Log.d(TAG, "hostVerify compareTemplates end");
            long timeEnd = System.currentTimeMillis();

            if (score >= 45) {
                log.postValue("[HOST VERIFY]\nSuccess\nSimilar score : " + score + "\nCapture Time : " + (getImageEndTime - start) + "ms\nVerify Time : " + (timeEnd - getImageEndTime) + "ms");
            } else if (score >= 0) {
                log.postValue("[HOST VERIFY]\nFailed\nSimilar score : " + score + "\nCapture Time : " + (getImageEndTime - start) + "ms\nVerify Time : " + (timeEnd - getImageEndTime) + "ms");
            } else {
                log.postValue("[HOST VERIFY]\nError\nCode : " + score + "\nTime : " + (timeEnd - start) + "ms");
            }
            busy.postValue(false);
        });
    }

    public void hostSearch() {
        executor.execute(() -> {
            if (bufferId.getValue() == null) bufferId.postValue(0);
            log.postValue("[HOST SEARCH]\nPlease wait...");
            busy.postValue(true);
            bm.postValue(null);
            templateMxImage = null;
            long start = System.currentTimeMillis();
            MxImage getImage = getImage();
            long getImageEndTime = System.currentTimeMillis();
            if (getImage.error != 0) {
                log.postValue("[HOST SEARCH]\nFail\nFAIL CODE: " + getImage.error);
                busy.postValue(false);
                return;
            }
            if (nfiq.getValue() != null && nfiq.getValue() && nfiqLevel.getValue() != null) {
                int nfiq = mJustouchApi.getNFIQ(getImage.data, getImage.width, getImage.height);
                if (nfiq < 0) {
                    log.postValue("[HOST SEARCH]\nNFIQ Failed\nFAIL Code: " + nfiq);
                    busy.postValue(false);
                    return;
                }
                if (nfiq > nfiqLevel.getValue()) {
                    log.postValue("[HOST SEARCH]\nFailed\nNFIQ reject");
                    busy.postValue(false);
                    return;
                }
            }
            if (lfd.getValue() != null && lfd.getValue()) {
                int[] lfdResult = {0};
                int i = FingerLiveApi.fingerLiveWithLevel(getImage.data, getImage.width, getImage.height, 3, lfdResult);
                if (i != 0) {
                    log.postValue("[HOST SEARCH]\nLFD Failed\nFAIL Code: " + i);
                    busy.postValue(false);
                    return;
                }
                if (lfdResult[0] == 0) {
                    log.postValue("[HOST SEARCH]\nFailed\nLFD reject");
                    busy.postValue(false);
                    return;
                }
            }
            showFingerImage(getImage);
            byte[] tempFeature = new byte[DiskTemplates.TEMPLATE_LENGTH];
            int result = createTemplate(getImage.data, getImage.width, getImage.height, tempFeature, null);
            if (result < 0) {
                log.postValue("[HOST SEARCH]\nFail\nGet new template failed!\nCode : " + result);
                busy.postValue(false);
                return;
            }
            int index = searchTemplates(tempFeature, mDiskTemplates.count(), mDiskTemplates.getAll());
            long timeEnd = System.currentTimeMillis();
            if (index >= 0) {
                String id = mDiskTemplates.getId(index);
                log.postValue("[HOST SEARCH]\nSuccess\nUser Id : " + id + "\nCapture Time : " + (getImageEndTime - start) + "ms\nSearch Time : " + (timeEnd - getImageEndTime) + "ms");
            } else {
                log.postValue("[HOST SEARCH]\nFailed\nThe finger is not enrolled\nCapture Time : " + (getImageEndTime - start) + "ms\nSearch Time : " + (timeEnd - getImageEndTime) + "ms");
            }
            busy.postValue(false);
        });
    }

    private int compareTemplates(byte[] data, byte[] dataAnother) {
        if (hostFeatureType.getValue() == null) hostFeatureType.postValue(0);
        if (hostFeatureType.getValue() == 0) {
            Log.d(TAG, "compareTemplatesMIAIXS");
            return mJustouchApi.compareTemplatesMIAIXS(data, dataAnother);
        } else if (hostFeatureType.getValue() == 1) {
            Log.d(TAG, "compareTemplatesISO");
            return mJustouchApi.compareTemplatesISO(data, dataAnother);
        } else if (hostFeatureType.getValue() == 2) {
            Log.d(TAG, "compareTemplatesISO2011");
            return mJustouchApi.compareTemplatesISO2011(data, dataAnother);
        } else if (hostFeatureType.getValue() == 3) {
            Log.d(TAG, "compareTemplatesANSI");
            return mJustouchApi.compareTemplatesANSI(data, dataAnother);
        } else {
            Log.d(TAG, "compareTemplatesANSI2009");
            return mJustouchApi.compareTemplatesANSI2009(data, dataAnother);
        }
    }

    private int createTemplate(byte[] data, int width, int height, byte[] newTemplate, int[] length) {
        if (hostFeatureType.getValue() == null) hostFeatureType.postValue(0);
        if (hostFeatureType.getValue() == 0) {
            Log.d(TAG, "createTemplateMIAIXS");
            return mJustouchApi.createTemplateMIAIXS(data, width, height, MIN_MINUTIAE_COUNT, newTemplate, length);
        } else if (hostFeatureType.getValue() == 1) {
            Log.d(TAG, "createTemplateISO");
            return mJustouchApi.createTemplateISO(data, width, height, MIN_MINUTIAE_COUNT, newTemplate, true, length);
        } else if (hostFeatureType.getValue() == 2) {
            Log.d(TAG, "createTemplateISO2011");
            return mJustouchApi.createTemplateISO2011(data, width, height, MIN_MINUTIAE_COUNT, newTemplate, true, length);
        } else if (hostFeatureType.getValue() == 3) {
            Log.d(TAG, "createTemplateANSI");
            return mJustouchApi.createTemplateANSI(data, width, height, MIN_MINUTIAE_COUNT, newTemplate, true, length);
        } else {
            Log.d(TAG, "createTemplateANSI2009");
            return mJustouchApi.createTemplateANSI2009(data, width, height, MIN_MINUTIAE_COUNT, newTemplate, length);
        }
    }

    private int createTemplate(int featureType, byte[] data, int width, int height, byte[] newTemplate, boolean ex, int[] length) {
        if (featureType == 0) {
            Log.d(TAG, "exportAsTemplate createTemplateMIAIXS");
            return mJustouchApi.createTemplateMIAIXS(data, width, height, MIN_MINUTIAE_COUNT, newTemplate, length);
        } else if (featureType == 1) {
            Log.d(TAG, "exportAsTemplate createTemplateISO");
            return mJustouchApi.createTemplateISO(data, width, height, MIN_MINUTIAE_COUNT, newTemplate, ex, length);
        } else if (featureType == 2) {
            Log.d(TAG, "exportAsTemplate createTemplateISO2011");
            return mJustouchApi.createTemplateISO2011(data, width, height, MIN_MINUTIAE_COUNT, newTemplate, ex, length);
        } else if (featureType == 3) {
            Log.d(TAG, "exportAsTemplate createTemplateANSI");
            return mJustouchApi.createTemplateANSI(data, width, height, MIN_MINUTIAE_COUNT, newTemplate, ex, length);
        } else {
            Log.d(TAG, "exportAsTemplate createTemplateANSI2009");
            return mJustouchApi.createTemplateANSI2009(data, width, height, MIN_MINUTIAE_COUNT, newTemplate, length);
        }
    }

    private int searchTemplates(byte[] templateToSearch, int numberOfDbTemplates, byte[] arrayOfDbTemplates) {
        if (hostFeatureType.getValue() == null) hostFeatureType.postValue(0);
        if (hostFeatureType.getValue() == 0) {
            Log.d(TAG, "searchTemplatesMIAIXS");
            return mJustouchApi.searchTemplatesMIAIXS(templateToSearch, numberOfDbTemplates, arrayOfDbTemplates);
        } else if (hostFeatureType.getValue() == 1) {
            Log.d(TAG, "searchTemplatesISO");
            return mJustouchApi.searchTemplatesISO(templateToSearch, numberOfDbTemplates, arrayOfDbTemplates);
        } else if (hostFeatureType.getValue() == 2) {
            Log.d(TAG, "searchTemplatesISO2011");
            return mJustouchApi.searchTemplatesISO2011(templateToSearch, numberOfDbTemplates, arrayOfDbTemplates);
        } else if (hostFeatureType.getValue() == 3) {
            Log.d(TAG, "searchTemplatesANSI");
            return mJustouchApi.searchTemplatesANSI(templateToSearch, numberOfDbTemplates, arrayOfDbTemplates);
        } else {
            Log.d(TAG, "searchTemplatesANSI2009");
            return mJustouchApi.searchTemplatesANSI2009(templateToSearch, numberOfDbTemplates, arrayOfDbTemplates);
        }
    }

    public void hostRemove() {
        String userIdValue = hostUserId.getValue();
        if (TextUtils.isEmpty(userIdValue)) {
            log.postValue("[HOST REMOVE]\nFail\nPlease input you id");
            return;
        }
        assert userIdValue != null;
        final String userId = userIdValue.trim();
        busy.postValue(true);
        boolean delete = mDiskTemplates.delete(userId);
        if (delete) {
            log.postValue("[HOST REMOVE]\nSuccess\nRemoved User Id: " + userId);
        } else {
            log.postValue("[HOST REMOVE]\nFailed\nNot found User Id: " + userId);
        }
        busy.postValue(false);
    }

    public void hostClear(String typeName) {
        executor.execute(() -> {
            mDiskTemplates.clear();
            log.postValue("[HOST CLEAR]\nSuccess\nCleared all " + typeName + " template");
        });
    }

    public void hostShowDB() {
        log.postValue("[HOST DB SHOW]\nSuccess\nEnrolled Users: " + mDiskTemplates.getIds());
    }

    public void changeAlgorithm(String typeName) {
        if (hostFeatureNameNow.equals(typeName)) return;
        hostFeatureNameNow = typeName;
        Log.d(TAG, "changeAlgorithm : " + typeName);
        File file = new File(mContext.getExternalFilesDir(null), typeName);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                log.postValue("Init database error !");
                return;
            }
        }
        int templateCount = mDiskTemplates.refreshTemplatesFromFile(file.getAbsolutePath());
        if (templateCount >= 0) {
            log.postValue("Init database successful!\nFeatureType : " + typeName + "\nCount :" + templateCount + "/" + DiskTemplates.MAX_FINGER_COUNT + "\nFile location : " + file.getAbsolutePath());
        } else {
            log.postValue("Init database error !");
        }
    }

    public void exportAsTemplate(int featureType, String featureTypeName) {
        log.setValue(null);
        if (templateMxImage == null) {
            log.postValue("[GET TEMPLATE DATA]\nFailed\nPlease get image at first !");
            return;
        }
        executor.execute(() -> {
            busy.postValue(true);
            byte[] template = new byte[DiskTemplates.TEMPLATE_LENGTH];
            int[] templateLength = new int[1];
            int result = createTemplate(featureType, templateMxImage.data, templateMxImage.width, templateMxImage.height, template, false, templateLength);
            if (result == 0) {
                File file = new File(mContext.getExternalFilesDir(null), "EXPORT");
                if (!file.exists()) file.mkdirs();
                String filePath = file.getAbsolutePath() + "/Template_" + featureTypeName + ".fmr";
                Log.d(TAG, "exportAsTemplate : " + filePath);
                String path = FileUtils.saveFile(filePath, template, templateLength[0]);
                log.postValue("[GET TEMPLATE DATA]\nSuccess\nFile Size : " + templateLength[0] + "\nFilePath : " + path);
            } else {
                log.postValue("[GET TEMPLATE DATA]\nFailed\nCode : " + result);
            }
            busy.postValue(false);
        });
    }

    public void exportAsImage(int imageType, String imageName) {
        log.setValue(null);
        if (templateMxImage == null) {
            log.postValue("[GET IMAGE DATA]\nFailed\nPlease get image at first !");
            return;
        }
        executor.execute(() -> {
            busy.postValue(true);
            long result = -99999;
            byte[] imageData = new byte[0];
            int[] imageLen = new int[1];
            String suffix = "";
            long startTime = System.currentTimeMillis();
            if (imageType == 0) {
                //bmp
                suffix = ".bmp";
                imageData = new byte[templateMxImage.width * templateMxImage.height + 1087];
                result = mJustouchApi.convertRawToBMP(templateMxImage.data, templateMxImage.width, templateMxImage.height, imageData);
                imageLen[0] = imageData.length;
            } else if (imageType == 1) {
                //raw
                suffix = ".raw";
                result = 0;
                imageData = templateMxImage.data;
                imageLen[0] = imageData.length;
            } else if (imageType == 2) {
                suffix = ".jpeg2000";
                imageData = new byte[20 * 1024];
                Log.d(TAG, "compressJPEG2000 start");
                result = mJustouchApi.compressJPEG2000(templateMxImage.data, templateMxImage.width, templateMxImage.height,
                        500, 10, imageData, imageLen);
                Log.d(TAG, "compressJPEG2000 end");
            } else if (imageType == 3) {
                suffix = ".wsq";
                imageData = new byte[20 * 1024];
                result = mJustouchApi.compressWSQ(templateMxImage.data, templateMxImage.width, templateMxImage.height,
                        500, 6, imageData, imageLen);
            }
            if (result == 0) {
                File file = new File(mContext.getExternalFilesDir(null), "EXPORT");
                if (!file.exists()) file.mkdirs();
                String filePath = file.getAbsolutePath() + "/Image_" + imageName + suffix;
                String path = FileUtils.saveFile(filePath, imageData, imageLen[0]);
                log.postValue("[GET IMAGE DATA]\nSuccess\nTime : " + (System.currentTimeMillis() - startTime) + "ms\nFile Size : " + imageLen[0] + "\nFilePath : " + path);
            } else {
                log.postValue("[GET IMAGE DATA]\nFailed\nCode : " + result);
            }
            busy.postValue(false);
        });
    }

    public void exportAsFIR2005(int firType, String firName) {
        log.setValue(null);
        if (templateMxImage == null) {
            log.postValue("[GET FIR DATA]\nFailed\nPlease get image at first !");
            return;
        }
        executor.execute(() -> {
            busy.postValue(true);
            long result = -99999;
            byte[] firData = new byte[40 * 1024];
            int[] firLen = new int[1];
            long start = System.currentTimeMillis();
            long endTime = 0;
            if (firType == 0) {
                //jpeg2000
                //Compressed ratio [5, 15]
                result = mJustouchApi.makeFIR(templateMxImage.data, templateMxImage.width, templateMxImage.height,
                        ISOANSI.TYPE_J2K, 6, firData, firLen);
                endTime = System.currentTimeMillis();
            } else if (firType == 1) {
                //wsq
                //Compressed ratio [5, 15]
                result = mJustouchApi.makeFIR(templateMxImage.data, templateMxImage.width, templateMxImage.height,
                        ISOANSI.TYPE_WSQ, 6, firData, firLen);
                endTime = System.currentTimeMillis();
            }
            if (result == 0) {
                File file = new File(mContext.getExternalFilesDir(null), "EXPORT");
                if (!file.exists()) file.mkdirs();
                String filePath = file.getAbsolutePath() + "/FIR_" + firName + "(2005).fir";
                String path = FileUtils.saveFile(filePath, firData, firLen[0]);
                log.postValue("[GET FIR DATA]\nSuccess\nFile Size : " + firLen[0] + "\nTime: " + (endTime - start) + "ms\nFilePath : " + path);
            } else {
                log.postValue("[GET FIR DATA]\nFailed\nCode : " + result);
            }
            busy.postValue(false);
        });
    }

    public void exportAsFIR2011(int firType, String firName) {
        log.setValue(null);
        if (templateMxImage == null) {
            log.postValue("[GET FIR DATA]\nFailed\nPlease get image at first !");
            return;
        }
        executor.execute(() -> {
            busy.postValue(true);
            long result = -99999;
            byte[] firData = new byte[40 * 1024];
            int[] firLen = new int[1];
            long start = System.currentTimeMillis();
            long endTime = 0;
            if (firType == 0) {
                //jpeg2000
                //Compressed ratio [5, 15]
                result = mJustouchApi.makeFIR2011(templateMxImage.data, templateMxImage.width, templateMxImage.height,
                        ISOANSI.TYPE_J2K, 6, firData, firLen);
                endTime = System.currentTimeMillis();
            } else if (firType == 1) {
                //wsq
                //Compressed ratio [5, 15]
                result = mJustouchApi.makeFIR2011(templateMxImage.data, templateMxImage.width, templateMxImage.height,
                        ISOANSI.TYPE_WSQ, 6, firData, firLen);
                endTime = System.currentTimeMillis();
            }
            if (result == 0) {
                File file = new File(mContext.getExternalFilesDir(null), "EXPORT");
                if (!file.exists()) file.mkdirs();
                String filePath = file.getAbsolutePath() + "/FIR_" + firName + "(2011).fir";
                String path = FileUtils.saveFile(filePath, firData, firLen[0]);
                log.postValue("[GET FIR DATA]\nSuccess\nFile Size : " + firLen[0] + "\nTime: " + (endTime - start) + "ms\nFilePath : " + path);
            } else {
                log.postValue("[GET FIR DATA]\nFailed\nCode : " + result);
            }
            busy.postValue(false);
        });
    }

    public void nfiq() {
        log.setValue(null);
        if (templateMxImage == null) {
            log.postValue("[NFIQ]\nFailed\nPlease get image at first !");
            return;
        }
        executor.execute(() -> {
            busy.postValue(true);
            Log.d(TAG, "width: " + templateMxImage.width + " height: " + templateMxImage.height);
            int nfiq = mJustouchApi.getNFIQ(templateMxImage.data, templateMxImage.width, templateMxImage.height);
            if (nfiq >= 1 && nfiq <= 5) {
                String[] NFIQLevel = {"excellent", "veryGood", "good", "fair", "poor"};
                log.postValue("[NFIQ]\nSuccess\nCurrent image's NFIQ  : " + NFIQLevel[nfiq - 1] + " (" + nfiq + ")");
            } else {
                log.postValue("[NFIQ]\nFailed\nCode : " + nfiq);
            }
            busy.postValue(false);
        });
    }

    public void minutae() {
        log.setValue(null);
        if (templateMxImage == null) {
            log.postValue("[MINUTAE]\nFailed\nPlease get image at first !");
            return;
        }
        executor.execute(() -> {
            busy.postValue(true);
            byte[] img2 = new byte[templateMxImage.data.length * 3];
            System.arraycopy(templateMxImage.data, 0, img2, 0, templateMxImage.data.length);
            int result = mJustouchApi.drawPointImg(img2, templateMxImage.width, templateMxImage.height);
            if (result == 0) {
                byte[] imageDate = new byte[img2.length + 1078];
                int raw2Bmp = mJustouchApi.convertRawToBMPRGB(img2, templateMxImage.width, templateMxImage.height, imageDate);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageDate, 0, imageDate.length);
                bm.postValue(bitmap);
                templateMxImage = null;
                log.postValue("[MINUTAE]\nSuccess");
            } else {
                log.postValue("[MINUTAE]\nFailed\nCode : " + result);
            }
            busy.postValue(false);
        });
    }

    //GenerateKeyPair
    public void generateKeyPair1() {
        log.setValue(null);
        busy.setValue(true);
        //executor.execute(() -> {
        //    MxResult<Boolean> result = mSm93MApi.genRSAKeyAir(1);
        //    if (result.isSuccess()) {
        //        log.postValue("[GENERATE KEY PAIR]\nSuccess");
        //    } else {
        //        log.postValue(transform("[GENERATE KEY PAIR]\nFailed", result));
        //    }
        //    busy.postValue(false);
        //});
    }

    public void readFtm() {
        log.setValue(null);
        busy.setValue(true);
        //executor.execute(() -> {
        //    MxResult<byte[]> result = mSm93MApi.readFTM();
        //    if (result.isSuccess()) {
        //        log.postValue("[READ FTM]\nSuccess\nFileContentLength : " + result.getData().length);
        //    } else {
        //        log.postValue(transform("[READ FTM]\nFailed", result));
        //    }
        //    busy.postValue(false);
        //});
    }
}