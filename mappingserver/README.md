## Readme For Python Gifticon Remapping Server

----

### How to install
```
$ python -m venv .venv
# python3 
$ ./.venv/Scripts/activate 
# or in windows : ./.venv/Scripts/Activate.ps1 
$ pip install -r requirements.txt
```

### How to Use
```
$ python crypt.py
# will run remapping server
```
### API Documentation

```
POST:
/gifticon/encrypt/register/:barcodeNum
{success : Boolean, reason: String}
```

```
POST:
/gifticon/encrypt/useqrcode

return
{success : false, reason: String}
else
{
    success : true, 
    data : ProductData(barcodeNum, productName, expireDate, category, price, used)
}

```

### Error Codes

- 406: Expired Barcode
- 408: Switchcon Timeout
- 409: Hash Check Failure
- 410: Used Barcode
- 400: Else

