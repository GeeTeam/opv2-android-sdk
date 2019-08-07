# opv2-android-sdk
onepass v2版本android客户端sdk

# 概述与资源

Android SDK提供给集成Android原生客户端开发的开发者使用。

## 环境需求

条目	|资源
------	|------------
开发目标|4.0以上
开发环境|Android Studio 2.1.3
系统依赖|`v7包`
sdk三方依赖|无

# 安装

## 获取SDK

1. 在demo的`libs`包下，将获取的`.aar`文件拖拽到工程中的libs文件夹下。

2. 在拖入`.aar`到libs文件夹后, 还要检查`.aar`是否被添加到**Library**,要在项目的
build.gradle下添加如下代码：

	```java
	repositories {
		flatDir {
	  		dirs 'libs'
		}
	}

	```

	并且要手动将aar包添加依赖：

	```java
	compile(name: 'geetest_onepassv2_android_vx.y.z', ext: 'aar')

	```

3. 添加权限

	```java
	 <uses-permission android:name="android.permission.WRITE_SETTINGS" />
	 <uses-permission android:name="android.permission.READ_PHONE_STATE" />
	 <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
	 <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	 <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
	 <uses-permission android:name="android.permission.INTERNET" />
	 <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />

	```

## 配置接口

开发者集成客户端sdk前, 必须先在您的服务器上搭建相应的 **服务端SDK** ，配置 **verifyUrl** ，并配置从 **极验后台** 获取的customId。这里以服务端 **verifyUrl** 配置成功，客户端开发步骤为例，如下：

1. 配置初始化接口

	```java
	OnePassHelper.with().init(MainActivity.this);

	```

2. 调用校验接口

	```java
	OnePassHelper.with().getToken(phone,custom, onePassListener);
	//第一个参数为输入的手机号码
	//第二个参数为所需要配置的CUSTOM_ID
	//第三个参数为所需实现监听回调结果接口

	```

>集成代码参考下方的 **代码示例**

# 代码示例

## 初始化与校验

### 初始化

在项目的具体页面的`onCreate`方法里面进行初始化。

```java
OnePassHelper.with().init(MainActivity.this);

```

### 调用校验

```java
OnePassHelper.with().getToken(phone,custom, onePassListener);

```

### 接口实现

实现接口进行校验。

```java
OnePassListener onePassListener =new OnePassListener() {
	@Override
	public void onTokenFail(JSONObject jsonObject) {
		//过程中出现的错误, 具体参考下方错误码
	}

	@Override
	public void onTokenSuccess(JSONObject jsonObject) {
		//sdk输出的参数,通过这些参数请求verifyUrl判断是否是本机号
	}

};

```

### 页面关闭

在页面关闭的时候执行此方法。

```java
@Override
protected void onDestroy() {
 	super.onDestroy();
 	OnePassHelper.with().cancel();
}

```

# SDK方法说明

## 初始化

### 方法描述

```
public void init(Context context)
```

### 参数说明

参数	|类型 |说明|
------	|-----|-----|
context|Context|上下文|

## 调用校验

### 方法描述

costomID：产品id，请在官网申请

```
public void getToken(String phone,String custom,OnePassListener Listener)
```

### 参数说明

参数	|类型 |说明|
------	|-----|-----|
phone|String|用户所填的手机号|
custom|String|极验后台配置唯一id|
onePassListener | OnePassListener |回调监听器，需要开发者自己实现|

## OnePassListener实现接口

### 错误回调

##### 方法说明

整个流程出现错误的时候调用

```java
public onTokenFail(JSONObject jsonObject)
```

#### 参数说明

参数	|类型 |说明|
------	|-----|-----|
jsonObject | JSONObject |错误信息|

##### jsonObject的参数说明

key	|说明|
------	|-----|
`code`|错误码|
`process_id`|流水号|
`custom_id`|极验后台配置唯一id|
`metadata` |具体的错误原因|
`real_op`|实际调用的运营商|
`op`|客户端获取的运营商|
`clienttype`|客户端,1表示Android|
`sdk`|sdk的版本号|

### 网关成功回调

#### 方法说明

整个流程网关成功之后调用

```java
public onTokenSuccess(JSONObject jsonObject)
```

#### 参数说明

参数	|类型 |说明|
------	|-----|-----|
jsonObject | JSONObject |获取token的成功信息|

##### jsonObject的参数说明

key	|说明|
------	|-----|
`accessscode`|运营商校验用的token|
`process_id`|流水号|
`phone`|发往运营商的手机号|
`clienttype`|客户端,1表示Android|
`sdk`|sdk的版本号|

## 关闭验证

### 方法描述

在activity的onDestroy()方法中实现

```
public void cancel()
```

### 代码示例

```
OnePassHelper.with().cancel();
```

### 参数说明

无

## 内部方法

### 方法描述
获取SDK版本号

```
public void getVersion()
```

### 参数说明

无

### 代码示例

```
OnePassHelper.with().getVersion()
```

### 方法描述
获取存储手机号

```
public void getPhone()
```

### 参数说明

无

### 代码示例

```
OnePassHelper.with().getPhone();
```

### 方法描述
获取流水号

```
public void getProcessId()
```

### 参数说明

无

### 代码示例

```
OnePassHelper.with().getProcessId();
```

### 方法描述
设置超时时间,默认时间为8000ms.

```
public void setConnectTimeout()
```

### 参数说明

无

### 代码示例

```
OnePassHelper.with().setConnectTimeout();
```

## 混淆规则

```
-dontwarn com.geetest.onepassv2.**
-keep class com.geetest.onepassv2.** {
*;
}
-dontwarn com.cmic.sso.sdk.**
-keep class com.cmic.sso.sdk.** {
*;
}
```
## 日志打印

SDK提供部分日志，TAG为`OnePassV2`。

## ErrorCode

### OnePass

`OnePass`产品的错误代码

Code	|Description
----------|------------
-20100       |手机号未传
-20101       |custom未传
-20200       |当前网络不可用
-20201       |当前手机没有电话卡
-20202       |当前手机有电话卡但是未开启数据网络
-20203       |`ConnectivityManager`不存在
-20204       |WIFI下走数据流量出现错误
-20205       |检测当前走数据流量超时,请检测当前卡是否欠费
-20206       |开启`enableHIPRI`失败
-20207       |WIFI下请求切换网络失败
-30200       |SDK内部请求PreGateWay接口超时
-40101       |移动运营商获取token失败
-40104       |移动不支持的网络制式
-40201       |联通运营商获取token失败
-40204       |联通不支持的网络制式(不支持2G)
-40301       |电信运营商获取token失败
-40305       |电信不支持的网络制式(不支持2G, 3G)
-50100       |SDK内部请求PreGateWay接口解密失败
-50101       |SDK内部请求PreGateWay接口返回错误

## 常见错误

### 1. 全机型，全系统兼容吗？

答：本产品只兼容4.0以上系统，但是在某些特殊定制的手机上，Wifi下走数据网络的方法没有返回，会造成网关验证失败。已经确认的机型有：乐视2。

### 2. 如果网关校验不过是什么原因？

答：`onTokenFail`接口里有详细的错误原因，如果仍有疑虑，可以将日志保存下来，及时联系极验工作人员。

### 3. Demo的运行有什么注意问题？

答：需要申请对应的customId，并需配置相应的服务接口。这样，Demo就可以正常使用了。

### 4. 本产品的数据网络运营商是怎样判断的？

答：当前无论单卡还是双卡，都是以默认开启数据网络的卡进行网关校验的。其中，无法判断默认数据网络的卡有三种情况，这三种情况为：没有开启`READ_PHONE_STATE`权限；无法获取当前的`IMSI`；无法根据当前的`IMSI`来判断运营商。

### 5. 怎样与OneLogin一起使用？

答：在demo项目的app目录下有`geetest.gradle文件`，在控制台执行`./gradlew excludeAar`即可生成脱离了移动SDK的OnePass SDK,路径在`app\build\excludeaar`下，直接使用即可。

> 及时查看查看极验输出日志









