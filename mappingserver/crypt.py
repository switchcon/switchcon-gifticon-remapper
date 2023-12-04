from flask import Flask, request, jsonify
import os
import struct
import base64 
import math
import hashlib
import hmac
from peewee import *
from playhouse.shortcuts import model_to_dict
import time

app = Flask(__name__)

db = SqliteDatabase('productData.db')

class Products(Model):
    barcodeNum = TextField(primary_key=True)
    productName = TextField()
    expireDate = TextField()
    category = TextField()
    price = IntegerField()
    used = IntegerField()
    class Meta:
        database = db
class Registered(Model):
    hashstring = TextField(primary_key=True)
    barcodenum = TextField(null=False)
    class Meta:
        database = db
        constraints = ["SQL(FOREIGN KEY(barcodenum) REFERENCES products(barcodeNum))"]


key = bytearray([
        0x01,0x01,0x01,0x01,
        0x02,0x02,0x02,0x02,
        0x03,0x03,0x03,0x03,
        0x04,0x04,0x04,0x04,
        0x05,0x05,0x05,0x05,
        0x06,0x06,0x06,0x06,
        0x07,0x07,0x07,0x07,
        0x08,0x08,0x08,0x08
    ])

@app.before_request
def bef_req():
    db.connect()

@app.after_request
def aft_req(response):
    db.close()
    return response

@app.route("/gifticon/encrypt/register/<bid>", methods=['POST'])
def encrypt_register(bid):
    try:
        ff = Products.get(Products.barcodeNum == bid)
        now = time.time()
        if(ff.used == 1):
            return jsonify({"success": False, "reason": "Already Used barcode!"}), 410
        else:
            expiry = time.strptime(ff.expireDate, "%Y-%m-%d")
            nowstruct = time.gmtime(now)
            if expiry < nowstruct:
                return jsonify({"success": False, "reason": "Registering Expired Barcode!"}), 406
        body = request.data
        b64body = base64.b64decode(body)
        nonce = b64body[:16]
        hmac_bnum = b64body[16:48]
        hmac_sid = b64body[48:80]
        serverID_bytes = b64body[80:]
        bid_byte = bid.encode('utf-8')
        hmac_bnum_new = hmac.new(key, bid_byte, digestmod=hashlib.sha256).digest()
        h = hmac.new(key, digestmod=hashlib.sha256)
        h.update(nonce); h.update(serverID_bytes)
        hmac_sid_new = h.digest()
        if (hmac_bnum != hmac_bnum_new) or (hmac_sid != hmac_sid_new):
            return jsonify({"success": False, "reason": "Inconsitent on Encrypted Data"}), 409
        else:
            Registered.create(hashstring=base64.b64encode(hmac_bnum).decode("utf-8"), barcodenum=bid)
            return jsonify({"success" : True, "reason": "Registered Well!"}), 200
    except Exception as e:
        return jsonify({"success": False, "reason": repr(e)}), 400

@app.route("/gifticon/encrypt/useqrcode", methods=['POST'])
def encrypt_QR():
    try:
        body = request.data
        now = time.time()
        swcEnable = "swc:" == body[:4].decode("utf-8")
        if not swcEnable:
            thisbody = body.decode("utf-8")
            ff = Products.get(Products.barcodeNum == thisbody)
            if(ff.used == 1):
                return jsonify({"success": False, "reason": "Already Used!"}), 410
            else:
                expiry = time.strptime(ff.expireDate, "%Y-%m-%d")
                nowstruct = time.gmtime(now)
                if expiry < nowstruct:
                    return jsonify({"success": False, "reason": "Expired Barcode!"}), 406
                ff.used = 1
                ff.save()
                return jsonify({"success": True, "data": model_to_dict(ff)}), 200
        b64body = base64.b64decode(body[4:])
        time_ = b64body[:4]
        nonce = b64body[4:20]
        hmac_bnum = b64body[20:52]
        hmac_sid = b64body[52:84]
        serverID_bytes = b64body[84:]
        h = hmac.new(key, digestmod=hashlib.sha256)
        h.update(time_); h.update(nonce); h.update(serverID_bytes)
        hmac_sid_new = h.digest()
        exptime = int( (time_[0] << 24) + (time_[1] << 16) + (time_[2] << 8) + time_[3])
        nowtime = int(now)
        if (hmac_sid != hmac_sid_new):
            return jsonify({"success": False, "reason": "Inconsitent on Encrypted Data"}), 409
        if nowtime - exptime > 610:
            return jsonify({"success": False, "reason": "Switchcon Barcode Expired!"}), 408
        realname = Registered.get(Registered.hashstring == base64.b64encode(hmac_bnum).decode("utf-8")).barcodenum
        ff = Products.get(Products.barcodeNum == realname)
        if(ff.used == 1):
            return jsonify({"success": False, "reason": "Already Used!"}), 410
        else:
            expiry = time.strptime(ff.expireDate, "%Y-%m-%d")
            nowstruct = time.gmtime(now)
            if expiry < nowstruct:
                return jsonify({"success": False, "reason": "Expired Barcode!"}), 406
            ff.used = 1
            ff.save()
            return jsonify({"success": True, "data": model_to_dict(ff)}), 200
    except Exception as e:
        print(e.__class__) 
        return jsonify({"success": False, "reason": repr(e)}), 400

@app.route("/gifticon/additem", methods=["POST"])
def addItem():
    try:
        barcodeNum = request.args.get("barcodenum")
        productName = request.args.get("productname")
        category = request.args.get("category")
        price = int(request.args.get("price"))
        used = request.args.get("used")
        expireDate =request.args.get("expiredate")
        
        Products.create(barcodeNum=barcodeNum, productName=productName, category=category, price=price, used=used)
        return jsonify({"success" :  True}) , 200
    except Exception as e:
        return jsonify({"success" :  False, "reason" : repr(e)}) , 400


app.run(port=8088)

