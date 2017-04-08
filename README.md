# TinkerTest
Tinker hotfix 热修复框架使用demo

列举一些大家比较熟悉的一些热更新方案：
微信开源：Tinker
大众点评：Nuwa
阿里巴巴：Dexposed
阿里巴巴：AndFix
美团：Robust

Bugly也是出于高可用性的考虑，Tinker支持动态下发代码、So库以及资源，所以我们最终选择了Tinker方案作为我们SDK的一项能力。
这里有一点需要说明的，Android版的热更新SDK是包含在升级SDK里面的，所以如果你想使用我们提供的热更新能力需要下载对应版本的升级SDK, 注意：升级SDK自1.2.0起将不再支持以jar包形式集成，我们建议您使用Android studio并且以gradle方式集成。


热更新能力是Bugly为解决开发者紧急修复线上Bug，而无需重新发版让用户无感知就能把问题修复的一项能力。Bugly目前采用微信Tinker的开源方案，开发者只需要集成我们提供的SDK就可以实现自动下载补丁包、合成、并且应用补丁的功能，我们也提供了热更新管理后台让开发者对每个版本的补丁进行管理。
集成我们SDK的好处是显而易见的：

	* 无需关注Tinker是如何合成补丁的
	* 无需自己搭建补丁管理后台
	* 无需考虑后台下发补丁策略的任何事情
	* 无需考虑补丁下载合成的时机，处理后台下发的策略
	* 我们提供了更加方便集成Tinker的方式
	* 我们提供应用升级一站式解决方案

管理后台：https://beta.bugly.qq.com/apps/7d2d819bfa/hotfix?pid=1


如何集成Bugly热更新SDK？
     看文档、看文档、看文档。重要的事情说三遍。
     Android热更新接入指南

第一步：添加插件依赖工程根目录下“build.gradle”文件中添加：
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        // tinkersupport插件, 其中lastest.release指拉取最新版本，也可以指定明确版本号，例如1.0.4
        classpath "com.tencent.bugly:tinker-support:latest.release"
    }
}

注意：自tinkersupport 1.0.3版本起无需再配tinker插件的classpath。
版本对应关系:
tinker-support 1.0.4 对应 tinker 1.7.7
tinker-support 1.0.3 对应 tinker 1.7.6
tinker-support 1.0.2 对应 tinker 1.7.5（需配置tinker插件的classpath）

第二步：集成SDKgradle配置
在app module的“build.gradle”文件中添加（示例配置）：

 dependencies {
          compile "com.android.support:multidex:1.0.1" // 多dex配置
          compile 'com.tencent.bugly:crashreport_upgrade:latest.release' // 升级SDK
}


在app module的“build.gradle”文件中添加：
// 依赖插件脚本
apply from: 'tinker-support.gradle'
tinker-support.gradle内容如下所示（示例配置）：
注：您需要在同级目录下创建tinker-support.gradle这个文件哦。
apply plugin: 'com.tencent.bugly.tinker-support'

def bakPath = file("${buildDir}/bakApk/")


/**
 * 此处填写每次构建生成的基准包目录
 */def baseApkDir = "app-0208-15-10-00"

/**
 * 对于插件各参数的详细解析请参考
 */
 
 
tinkerSupport {

    // 开启tinker-support插件，默认值true
    enable = true

    // 指定归档目录，默认值当前module的子目录tinker
    autoBackupApkDir = "${bakPath}"

    // 是否启用覆盖tinkerPatch配置功能，默认值false
    // 开启后tinkerPatch配置不生效，即无需添加tinkerPatch
    overrideTinkerPatchConfiguration = true

    // 编译补丁包时，必需指定基线版本的apk，默认值为空
    // 如果为空，则表示不是进行补丁包的编译
    // @{link tinkerPatch.oldApk }
    baseApk = "${bakPath}/${baseApkDir}/app-release.apk"

    // 对应tinker插件applyMapping
    baseApkProguardMapping = "${bakPath}/${baseApkDir}/app-release-mapping.txt"

    // 对应tinker插件applyResourceMapping
    baseApkResourceMapping = "${bakPath}/${baseApkDir}/app-release-R.txt"

    // 构建基准包和补丁包都要指定不同的tinkerId，并且必须保证唯一性
    tinkerId = "base-1.0.1"

    // 构建多渠道补丁时使用
    // buildAllFlavorsDir = "${bakPath}/${baseApkDir}"

    // 是否开启反射Application模式
    enableProxyApplication = false

}



 // 一般来说,我们无需对下面的参数做任何的修改
 // 对于各参数的详细介绍请参考:
 //https://github.com/Tencent/tinker/wiki/Tinker-%E6%8E%A5%E5%85%A5%E6%8C%87%E5%8D%97

tinkerPatch {
    //oldApk ="${bakPath}/${appName}/app-release.apk"
    
    ignoreWarning = false
    useSign = true
    dex {
        dexMode = "jar"
        pattern = ["classes*.dex"]
        loader = []
    }
    lib {
        pattern = ["lib/*/*.so"]
    }

    res {
        pattern = ["res/*", "r/*", "assets/*", "resources.arsc", "AndroidManifest.xml"]
        ignoreChange = []
        largeModSize = 100
    }

    packageConfig {
    }
    sevenZip {
        zipArtifact = "com.tencent.mm:SevenZip:1.1.10"//        path = "/usr/local/bin/7za"
    }
    buildConfig {
        keepDexApply = false
        //tinkerId = "1.0.1-base"
        //applyMapping = "${bakPath}/${appName}/app-release-mapping.txt" //  可选，设置mapping文件，建议保持旧apk的proguard混淆方式
        //applyResourceMapping = "${bakPath}/${appName}/app-release-R.txt" // 可选，设置R.txt文件，通过旧apk文件保持ResId的分配
    }
}



更详细的配置项参考tinker-support配置说明
第三步：初始化SDKenableProxyApplication = false 的情况这是Tinker推荐的接入方式，一定程度上会增加接入成本，但具有更好的兼容性。
集成Bugly升级SDK之后，我们需要按照以下方式自定义ApplicationLike来实现Application的代码（以下是示例）：
自定义Application
public class SampleApplication extends TinkerApplication {
    public SampleApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "xxx.xxx.SampleApplicationLike",
                "com.tencent.tinker.loader.TinkerLoader", false);
    }
}

注意：这个类集成TinkerApplication类，这里面不做任何操作，所有Application的代码都会放到ApplicationLike继承类当中
参数解析
参数1：tinkerFlags 表示Tinker支持的类型 dex only、library only or all suuport，default: TINKER_ENABLE_ALL
参数2：delegateClassName Application代理类 这里填写你自定义的ApplicationLike
参数3：loaderClassName Tinker的加载器，使用默认即可
参数4：tinkerLoadVerifyFlag 加载dex或者lib是否验证md5，默认为false
我们需要您将以前的Applicaton配置为继承TinkerApplication的类：

自定义ApplicationLike
public class SampleApplicationLike extends DefaultApplicationLike {

    public static final String TAG = "Tinker.SampleApplicationLike";

    public SampleApplicationLike(Application application, int tinkerFlags,
            boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime,
            long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
        // 调试时，将第三个参数改为true
        Bugly.init(getApplication(), "900029763", false);
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        // 安装tinker
        // TinkerManager.installTinker(this); 替换成下面Bugly提供的方法
        Beta.installTinker(this);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallback(Application.ActivityLifecycleCallbacks callbacks) {
        getApplication().registerActivityLifecycleCallbacks(callbacks);
    }

}


注意：tinker需要你开启MultiDex,你需要在dependencies中进行配置compile "com.android.support:multidex:1.0.1"才可以使用MultiDex.install方法； SampleApplicationLike这个类是Application的代理类，以前所有在Application的实现必须要全部拷贝到这里，在onCreate方法调用SDK的初始化方法，在onBaseContextAttached中调用Beta.installTinker(this);。
enableProxyApplication = true 的情况

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
        // 调试时，将第三个参数改为true
        Bugly.init(this, "900029763", false);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);


        // 安装tinker
        Beta.installTinker();
    }

}


注：无须你改造Application，主要是为了降低接入成本，我们插件会动态替换AndroidMinifest文件中的Application为我们定义好用于反射真实Application的类（需要您接入SDK 1.2.2版本 和 插件版本 1.0.3以上）。
第四步：AndroidManifest.xml配置
1. 权限配置
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_LOGS" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

注意：如果你也想使用升级功能，你必须要进行2、3项的配置，而如果你只想使用热更新能力，你只需要配置权限即可。
2. Activity配置
<activity
    android:name="com.tencent.bugly.beta.ui.BetaActivity"
    android:theme="@android:style/Theme.Translucent" />
3. 配置FileProvider
注意：如果您想兼容Android N或者以上的设备，必须要在AndroidManifest.xml文件中配置FileProvider来访问共享路径的文件。如果你使用的第三方库也配置了同样的FileProvider，你需要将第三方库配置的路径copy到我们配置的provider_path文件下。
 <provider
    android:name="android.support.v4.content.FileProvider"
    android:authorities="${applicationId}.fileProvider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/provider_paths"/></provider>


${applicationId}请替换为您的包名，例如com.bugly.upgrade.demo。这里要注意一下，FileProvider类是在support-v4包中的，检查你的工程是否引入该类库。
在res目录新建xml文件夹，创建provider_paths.xml文件如下：

<?xml version="1.0" encoding="utf-8"?><paths xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- /storage/emulated/0/Download/${applicationId}/.beta/apk-->
    <external-path name="beta_external_path" path="Download/"/>
    <!--/storage/emulated/0/Android/data/${applicationId}/files/apk/-->
    <external-path name="beta_external_files_path" path="Android/data/"/></paths>


这里配置的两个外部存储路径是升级SDK下载的文件可能存在的路径，一定要按照上面格式配置，不然可能会出现错误。
第五步：混淆配置
为了避免混淆SDK，在Proguard混淆文件中增加以下配置：
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

如果你使用了support-v4包，你还需要配置以下混淆规则：
