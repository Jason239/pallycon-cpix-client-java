---------------------------------------
# PallyCon CPIX Client for Java
 This module shows the client code for CPIX communication with PallyCon KMS server in Java.



---------------------------------------
## Requirements

- JDK 17
- KMS token
  - This is an API authentication token that is generated when you sign up PallyCon service.



---------------------------------------
## Function parameters

 You can set the parameters of the function based on the packaging scenario you require. 

```java
public ContentPackagingInfo GetContentKeyInfoFromPallyConKMS(String contentId, EnumSet<DrmType> drmTypes, EncryptionScheme encryptionScheme, EnumSet<TrackType> trackTypes) throws CpixClientException;
```



```java
public enum DrmType {
	WIDEVINE,
	PLAYREADY,
	FAIRPLAY,
	NCG,
	HLS_NCG
}
```

```java
public enum EncryptionScheme {
	CENC,
	CBC1,
	CENS,
	CBCS
}
```

```java
public enum TrackType {
	ALL_TRACKS,
	AUDIO,
	SD,
	HD,
	UHD1,
	UHD2
}
```

You can set multiple values using `EnumSet`, such as `EnumSet.of(DrmType.WIDEVINE, DrmType.PLAYREADY, DrmType.FAIRPLAY)` and `EnumSet.of(TrackType.HD, TrackType.SD, TrackType.AUDIO)` for **DrmType** and **TrackType**



> NOTE : NCG and HLS_NCG types are only support single-key.



---------------------------------------

## Output json data format

Originally, CPIX Responses are received in XML format, but <u>for readability, represent the data in JSON format in junit test code.</u>

 The items of the output data includes all data such as hex encoded, base64 encoded, payload_only, etc. so that you can easily copy and paste it into each packaging service.

- **Single-key** sample

```json
{
  "content_id": "demotest",
  "content_key_list": [
    {
      "key_id_hex": "cbbc38b6b50dab53f8263e06b7382aa3",
      "key_id_b64": "y7w4trUNq1P4Jj4Gtzgqow==",
      "key_hex": "959dbd673980e1d1bdf2d36209495f4e",
      "key_b64": "lZ29ZzmA4dG98tNiCUlfTg==",
      "iv_hex": "30313233343536373839616263646566",
      "iv_b64": "MDEyMzQ1Njc4OWFiY2RlZg==",
      "widevine": {
        "pssh": "AAAATHBzc2gAAAAA7e+LqXnWSs6jyCfc1R0h7QAAACwIARIQy7w4trUNq1P4Jj4GtzgqoxoMaW5rYWVudHdvcmtzIghkZW1vdGVzdA==",
        "pssh_payload_only": "CAESEMu8OLa1DatT+CY+Brc4KqMaDGlua2FlbnR3b3JrcyIIZGVtb3Rlc3Q="
      },
      "playready": {
        "pssh": "AAACknBzc2gAAAAAmgTweZhAQoarkuZb4IhflQAAAnJyAgAAAQABAGgCPABXAFIATQBIAEUAQQBEAEUAUgAgAHgAbQBsAG4AcwA9ACIAaAB0AHQAcAA6AC8ALwBzAGMAaABlAG0AYQBzAC4AbQBpAGMAcgBvAHMAbwBmAHQALgBjAG8AbQAvAEQAUgBNAC8AMgAwADAANwAvADAAMwAvAFAAbABhAHkAUgBlAGEAZAB5AEgAZQBhAGQAZQByACIAIAB2AGUAcgBzAGkAbwBuAD0AIgA0AC4AMwAuADAALgAwACIAPgA8AEQAQQBUAEEAPgA8AFAAUgBPAFQARQBDAFQASQBOAEYATwA+ADwASwBJAEQAUwA+ADwASwBJAEQAIABBAEwARwBJAEQAPQAiAEEARQBTAEMAVABSACIAIABDAEgARQBDAEsAUwBVAE0APQAiAEgARQBzAHYAbABYAFUAeABuAFcAdwA9ACIAIABWAEEATABVAEUAPQAiAHQAagBpADgAeQB3ADIAMQBVADYAdgA0AEoAagA0AEcAdAB6AGcAcQBvAHcAPQA9ACIAPgA8AC8ASwBJAEQAPgA8AC8ASwBJAEQAUwA+ADwALwBQAFIATwBUAEUAQwBUAEkATgBGAE8APgA8AEwAQQBfAFUAUgBMAD4AaAB0AHQAcABzADoALwAvAGwAaQBjAGUAbgBzAGUALgBwAGEAbABsAHkAYwBvAG4ALgBjAG8AbQAvAHIAaQAvAGwAaQBjAGUAbgBzAGUATQBhAG4AYQBnAGUAcgAuAGQAbwA8AC8ATABBAF8AVQBSAEwAPgA8AC8ARABBAFQAQQA+ADwALwBXAFIATQBIAEUAQQBEAEUAUgA+AA==",
        "pssh_payload_only": "cgIAAAEAAQBoAjwAVwBSAE0ASABFAEEARABFAFIAIAB4AG0AbABuAHMAPQAiAGgAdAB0AHAAOgAvAC8AcwBjAGgAZQBtAGEAcwAuAG0AaQBjAHIAbwBzAG8AZgB0AC4AYwBvAG0ALwBEAFIATQAvADIAMAAwADcALwAwADMALwBQAGwAYQB5AFIAZQBhAGQAeQBIAGUAYQBkAGUAcgAiACAAdgBlAHIAcwBpAG8AbgA9ACIANAAuADMALgAwAC4AMAAiAD4APABEAEEAVABBAD4APABQAFIATwBUAEUAQwBUAEkATgBGAE8APgA8AEsASQBEAFMAPgA8AEsASQBEACAAQQBMAEcASQBEAD0AIgBBAEUAUwBDAFQAUgAiACAAQwBIAEUAQwBLAFMAVQBNAD0AIgBIAEUAcwB2AGwAWABVAHgAbgBXAHcAPQAiACAAVgBBAEwAVQBFAD0AIgB0AGoAaQA4AHkAdwAyADEAVQA2AHYANABKAGoANABHAHQAegBnAHEAbwB3AD0APQAiAD4APAAvAEsASQBEAD4APAAvAEsASQBEAFMAPgA8AC8AUABSAE8AVABFAEMAVABJAE4ARgBPAD4APABMAEEAXwBVAFIATAA+AGgAdAB0AHAAcwA6AC8ALwBsAGkAYwBlAG4AcwBlAC4AcABhAGwAbAB5AGMAbwBuAC4AYwBvAG0ALwByAGkALwBsAGkAYwBlAG4AcwBlAE0AYQBuAGEAZwBlAHIALgBkAG8APAAvAEwAQQBfAFUAUgBMAD4APAAvAEQAQQBUAEEAPgA8AC8AVwBSAE0ASABFAEEARABFAFIAPgA="
      },
      "fairplay": {
        "key_uri": "skd://y7w4trUNq1P4Jj4Gtzgqow=="
      }
    }
  ]
}
```



#### The XML value in the CPIX Response that matches the key value in the json data

| Json key                                 | CPIX Response                                         |
| ---------------------------------------- | ----------------------------------------------------- |
| content_id                               | `id` attribute in **`<cpix:CPIX>`** tag               |
| key_id                                   | `kid` attribute in **`<cpix:ContentKey>`** tag        |
| key                                      | Text value of **`<pskc:PlainValue>`** tag             |
| iv                                       | `explicitIV` attribute in **`<cpix:ContentKey>`** tag |
| pssh (Widevine & PlayReady)              | Text value of **`<cpix:PSSH>`** tag                   |
| pssh_payload_only (Widevine & PlayReady) | Text value of **`<cpix:ContentProtectionData>`** tag  |
| key_uri (FairPlay)                       | Text value of **`<cpix:URIExtXKey>`** tag             |

For CPIX Response, please refer to [PallyCon CPIX API Guide](https://pallycon.com/docs/ko/multidrm/packaging/cpix-api/).



---------------------------------------

## References
- https://dashif-documents.azurewebsites.net/Cpix/master/Cpix.html
- https://pallycon.com/docs/en/multidrm/packaging/cpix-api/

---------------------------------------
