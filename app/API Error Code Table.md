## Error code table

### Drive

| **Error code** | **Macro definition**  |                       **Description**                        |
| :------------: | :-------------------: | :----------------------------------------------------------: |
|       0        |        ERR_OK         |                           Success                            |
|       1        |       ERR_FAIL        |                       Operation failed                       |
|       2        |        ERR_CMD        |                        Command error                         |
|       3        |       ERR_PARA        |                       Parameter error                        |
|       10       |      ERR_IOSEND       |         IO communication fails to send data packets          |
|       11       |      ERR_IORECV       |        IO communication fails to receive data packets        |
|       12       |     ERR_HEAD_FLAG     |                    Header-of-packet error                    |
|       13       |     ERR_END_FLAG      |                     End-of-packet error                      |
|       14       |        ERR_CRC        |                       Data check error                       |
|       15       |     ERR_DATA_LEN      |                      Data length error                       |
|       16       |     ERR_IMG_WDITH     |                      Image width error                       |
|       17       |    ERR_IMG_HEIGHT     |                      Image height error                      |
|       18       |    ERR_MEMORY_OVER    |                       Memory overflow                        |
|       19       |      ERR_KEY_LEN      | Symmetric encryption and decryption key length is not 32 bytes |
|       20       |    ERR_ENDATA_LEN     | Symmetrically encrypted data length is not an integer multiple of 16 bytes |
|       21       |     ERR_DEV_MODE      |              Mode error, can't upload pictures               |
|       22       |  ERR_PACKAGE_STATUS   |                     Package status error                     |
|       23       |   ERR_BASE64_ENCODE   |                    Base64 encoding error                     |
|       24       |       ERR_X509        |                    X509 certificate error                    |
|       25       |  ERR_PUBKEY_ENCRYPT   |               RSA public key encryption error                |
|       30       |  ERR_RESIDUAL_FINFER  |                     Fingerprint residue                      |
|       31       |    ERR_FINFER_NUM     |          The number of fingerprints is out of range          |
|       32       |    ERR_FINFER_TYPE    |                    Wrong fingerprint type                    |
|       33       |  ERR_FINGER_TIMEOUT   |               Fingerprint collection timed out               |
|       34       |    ERR_CAPTURE_FP     |                   Failed to acquire image                    |
|       35       |      ERR_DIGEST       |                        Summary error                         |
|       36       |    ERR_SESSIONKEY     |                Failed to generate session key                |
|       37       |      ERR_AESGCM       |                  AES/GCM encryption failed                   |
|       38       |      ERR_CANCEL       |                     Cancel the operation                     |
|       39       |       ERR_MODEL       |                          Mode error                          |
|       40       |    ERR_IMAGE_FAIL     |                     Failed to read image                     |
|       41       |     ERR_TIME_OUT      |                      Command timed out                       |
|       42       |  ERR_NO_SESSION_KEY   |                 No communication session key                 |
|       43       |    ERR_MATCH_FAIL     |                         Match failed                         |
|       44       |    ERR_GEN_TZ_FAIL    |                  Failed to generate feature                  |
|       60       |     ERR_NO_FINGER     |                          no fingers                          |
|       61       |    ERR_BAD_QUALITY    |          The fingerprint image quality is not good           |
|       70       |   ERR_MERGE_MB_FAIL   |                Failed to synthesize template                 |
|       71       |    ERR_SEARCH_FAIL    |                        Search failed                         |
|       72       | ERR_INVALID_BUFFER_ID |                  Incorrect Buffer ID value                   |
|       73       |  ERR_INVALID_TMPL_ID  |                     invalid template id                      |
|       74       |  ERR_TMPL_NOT_EMPTY   |      A template already exists at the specified number       |
|       75       |    ERR_TMPL_EMPTY     |          No template exists at the specified number          |
|       76       |  ERR_DUPLICATION_ID   |            The fingerprint is already registered             |
|       77       |  ERR_NO_ID_AVAILABLE  |                     No free template ID                      |
|       90       |       ERR_FLASH       |                         FLASH error                          |

### JustouchFingerAPI

| **Error code** |   **Macro definition**    |                       **Description**                        |
| :------------: | :-----------------------: | :----------------------------------------------------------: |
|       0        |          ERR_OK           |                           Success                            |
|    -100000     |      ERR_INIT_FAILED      |                    Initialization failed                     |
|    -100001     |        ERR_NO_INIT        |                        Uninitialized                         |
|    -100002     |        ERR_EXPIRED        |                    Authorization expired                     |
|    -100003     | ERR_LICENSE_SIZE_INVALID  |             Incorrect authorization code length              |
|    -100004     |    ERR_NO_LICENSE_FILE    |                 Authorization file not found                 |
|    -100005     | ERR_LICENSE_VERITY_FAILED |            Authorization code verification failed            |
|    -100006     |  ERR_IMAGE_CHECK_FAILED   |                      Image check failed                      |
|    -100007     |  ERR_IMAGE_DATA_INVALID   |                  Illegal image data format                   |
|    -100008     |  ERR_IMAGE_SIZE_INVALID   |                      Illegal image size                      |
|    -100009     |     ERR_MEMORY_FAILED     |                    Memory request failed                     |
|    -100010     |    ERR_EXTRACT_FAILED     |            Fingerprint feature extraction failed             |
|    -100011     |     ERR_MATCH_FAILED      |            Fingerprint feature comparison failed             |
|    -100012     |    ERR_COMPRESS_FAILED    |                   Image compression failed                   |
|    -100013     |   ERR_DECOMPRESS_FAILED   |                  Image decompression failed                  |
|    -100014     | ERR_GEN_IMAGE_CHECK_CODE  |             Failed to generate image check code              |
|    -100015     |   ERR_PARAMETER_INVALID   |                      Invalid parameter                       |
|    -100016     |    ERR_QUALITY_FAILED     |                 Failed to get image quality                  |
|    -100017     |   ERR_DEVICE_SN_LENGTH    |                     Device SN exception                      |
|    -100018     |   ERR_DEVICE_ID_LENGTH    |                     Device ID exception                      |
|    -100019     | ERR_BLUEBOOTH_ADDR_LENGTH |                 Bluetooth address exception                  |
|    -100020     |       ERR_SAVE_FILE       |                       Save file error                        |
|    -411001     |    ERROR_ALG_MINUTIAE     |                      ERROR ALG MINUTIAE                      |
|    -411002     |     ERROR_ALG_SEARCH      |                       ERROR ALG SEARCH                       |
|    -411000     |       ERROR_RESULT        |                         ERROR RESULT                         |
|    -410001     |       ERROR_LIC_ID        |                  Unable to encode IMEI, SN                   |
|    -410002     |      ERROR_LIC_FAIL       |               Failed to get authorization file               |
|    -410003     |     ERROR_LIC_STORAGE     |          Failed to read or write authorization file          |
|    -410004     |       ERROR_LIC_NET       |      Network exception, unable to connect to the server      |
|    -410005     |       ERR_ANDROID_Q       |                   Android 10 not supported                   |
|    -410006     |  ERR_ANDROID_PERMISSION   |                   Unauthorized permission                    |
|    -410007     |   ERROR_LIC_SERVER_BASE   | The connection is normal, but the authorization server is abnormal |

### FingerApi

| **Error code** |         Macro definition         |               Description               |
| :------------: | :------------------------------: | :-------------------------------------: |
|       0        |           SUCCESS_CODE           |                 Success                 |
|    -524001     |       SDK_NOT_INITIALIZED        |           SDK NOT INITIALIZED           |
|    -524002     |       DEVICE_NOT_CONNECTED       |          DEVICE NOT CONNECTED           |
|    -524003     |      READ_OR_WRITE_TIMEOUT       |          READ OR WRITE TIMEOUT          |
|    -524004     |       ACQUISITION_TIMEOUT        |           ACQUISITION TIMEOUT           |
|    -524005     |            NO_DEVICE             |                NO DEVICE                |
|    -524006     |          NO_PERMISSION           |              NO PERMISSION              |
|    -524007     |   FAILED_TO_WRITE_OR_READ_DATA   |      FAILED TO WRITE OR READ DATA       |
|    -524008     |       FAILED_TO_WRITE_DATA       |          FAILED TO WRITE DATA           |
|    -524009     |       FAILED_TO_READ_DATA        |           FAILED TO READ DATA           |
|    -524010     |         CSW_FORMAT_ERROR         |            CSW FORMAT ERROR             |
|    -524011     |  SCSI_COMMAND_EXECUTION_FAILED   |      SCSI COMMAND EXECUTION FAILED      |
|    -524012     |        SCSI_ERROR_NEED_RE        | SCSI ERROR OCCURS AND NEEDS TO BE RESET |
|    -524013     | INSTRUCTION_EXECUTION_EXCEPTION  |     INSTRUCTION EXECUTION EXCEPTION     |
|    -524014     |    MOD_TEMPLATE_FORMAT_ERROR     |        MOD TEMPLATE FORMAT ERROR        |
|    -524015     | LATENT_INITIALIZATION_EXCEPTION  |     LATENT INITIALIZATION EXCEPTION     |
|    -524016     |         LATENT_EXCEPTION         |            LATENT EXCEPTION             |
|    -524017     |          NFIQ_EXCEPTION          |             NFIQ EXCEPTION              |
|    -524018     |   LFD_INITIALIZATION_EXCEPTION   |      LFD INITIALIZATION EXCEPTION       |
|    -524019     |          LFD_EXCEPTION           |              LFD EXCEPTION              |
|    -524020     |          AREA_EXCEPTION          |             AREA EXCEPTION              |
|    -524021     |          X509_EXCEPTION          |             X509 EXCEPTION              |
|    -524022     | RSA_PUBLIC_KEY_ENCRYPTION_FAILED |    RSA PUBLIC KEY ENCRYPTION FAILED     |
|    -524023     |       BASE64_DECODE_FAILED       |          BASE64 DECODE FAILED           |
|    -524024     |        RSA_ENCRYPT_FAILED        |           RSA ENCRYPT FAILED            |
|    -524025     |        DES_ENCRYPT_FAILED        |           DES ENCRYPT FAILED            |
|    -524026     |        DES_DECRYPT_FAILED        |           DES DECRYPT FAILED            |
|    -524027     |         AES_INIT_FAILED          |             AES INIT FAILED             |
|    -524028     |        AES_ENCRYPT_FAILED        |           AES ENCRYPT FAILED            |
|    -524029     |        AES_DECRYPT_FAILED        |           AES DECRYPT FAILED            |
|    -524030     |         PARAMETER_ERROR          |             PARAMETER ERROR             |

