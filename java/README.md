## Readme For Python Gifticon Remapping Server

----

### How to Use

just import .java file
```
import ...SwitchconHMACAuthenticator
```
to make test strings:
```
java TestSwitchconAuthenticator
// classpath and bcprov-jdk15on-... jar needed
```
### API Documentation

```
getAuthStringRegister(String barcodeNumber, String serverID, byte[] keyBytes) : returns String (b64encoded)

getAuthStringQRUse(String barcodeNumber, String serverID, byte[] keyBytes) : returns String (swc:.../b64encoded)
```



