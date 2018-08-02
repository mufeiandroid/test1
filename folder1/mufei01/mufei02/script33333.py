#!/usr/bin/env python
# encoding: utf-8
# 访问 http://tool.lu/pyc/ 查看更多信息
from xml.etree import ElementTree as ET
from xml.etree.ElementTree import SubElement
from xml.etree.ElementTree import Element
from xml.etree.ElementTree import ElementTree
import os
import platform
import shutil
import file_operate
import error_operate
import xml.etree.ElementTree as ET
androidNS = 'http://schemas.android.com/apk/res/android'
# attr_authorities = '{' + androidNS + '}authorities'
attr_Name = '{' + androidNS + '}name'
attr_Host = '{' + androidNS + '}host'
attr_Scheme = '{' + androidNS + '}scheme'

# categoryName = 'android.intent.category.DEFAULT'
# INTENT_FILTER = '<intent-filter><action android:name="android.intent.action.VIEW" /><category android:name="android.intent.category.DEFAULT" /><category android:name="android.intent.category.BROWSABLE" /><data android:host="hy" android:scheme="@string/facebook_app_id" /></intent-filter>'
INTENT_MAIN = 'android.intent.action.MAIN'

def script(SDK, decompileDir, packageName, usrSDKConfig):
    ManifestDir = os.path.join(decompileDir, 'AndroidManifest.xml')
    ET.register_namespace('android', androidNS)
    tree = ET.parse(ManifestDir)
    root = tree.getroot()
    application = root.find('application')
    # application.set(attr_Name, 'com.quicksdk.QuickSdkApplication')
    activitys = application.findall('activity')
    for mainAct in activitys:
        value_name = mainAct.get(attr_Name, 'null')
        tag_intent = mainAct.find('intent-filter')
        if None is tag_intent:
            continue
        keys = tag_intent.getchildren()
        if len(keys) == 0:
            continue
        if None is not tag_intent:
            action = tag_intent.find('action')
            if None is action:
                continue
            actionValue = action.get(attr_Name, 'null')
            if actionValue == INTENT_MAIN:
                intentFilterNode = SubElement(mainAct, 'intent-filter')
                actionNode2 = SubElement(intentFilterNode, 'action')
                actionNode2.set(attr_Name, 'android.intent.action.VIEW')
                actionNode3 = SubElement(intentFilterNode, 'category')
                actionNode3.set(attr_Name, 'android.intent.category.DEFAULT')
                actionNode4 = SubElement(intentFilterNode, 'category')
                actionNode4.set(attr_Name, 'android.intent.category.BROWSABLE')
                actionNode5 = SubElement(intentFilterNode, 'data')  
                actionNode5.set(attr_Host, 'hy')
                actionNode5.set(attr_Scheme, '@string/facebook_app_id')


                # action.set(attr_Name, packageName)
                # category = tag_intent.find('category')
                # category.set(attr_Name, categoryName)
                # print value_name, actionValue
            
    tree.write(ManifestDir, 'utf-8')
    file_operate.modifyFileContent(ManifestDir, '.xml', 'packagename',packageName)