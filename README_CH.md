ApkEditor
========

ApkEditor是一个Android应用的Gradle插件. 

在Android应用中经常会引用一些lib或者aar，但这些第三方的包不可避免会携带一些冗余的资源, 使你的apk变得很大。本插件可以把这些资源从你的apk中剔除出来， 使你的apk变得尽可能得小。

但是， ApkEditor需要你有能力可以分辨这些资源在运行时不会被用到。否则会造成程序的崩溃。

下载和使用
--------

1. ApkEditor需要检查你的Android应用的运行环境。 所以请保证`com.android.application`已经被使用。

2. 应用到项目:

	```
	buildscript {
	    repositories {
	        jcenter()
	    }
	    dependencies {
	        classpath 'com.mapeiyu.apkeditor:apkeditor:1.0.0'
	    }
	}
	apply plugin: 'apkeditor'
	```
3. 使用`apkeditor` 插件DSL:

	```
	apkeditor {
	    exclude '/assets/**'
	    release {
	        exclude 'lib/**'
	    }
	    debug {
	        exclude 'res/**/*.png'
	        exclude 'res/**/*.xml'
	    }
	    //如果你设置了一个名为black的flavor
	    blackDebug { 
	    	exclude 'lib/**'
		}
	}
	```
4. 然后正常构建或者安装. 生成的apk将会被打包成不含上述规则的资源

解释
----------

* 在根节点下的`exclude`， 将会应用到所有的buildType和flavor产生的 apk中.
* 指定节点下的`exclude`， 比如`release debug backDebug`, 只能应用到指定的apk中.
* `exclude`可以被多次调用.
* `exclude`的设置规则遵循 [Java Filesystem API][1], 并且匹配来源来自于apk的文件结构. (你可解压缩apk, 或者通过Android Studio的分析工具 `Build->Analyze APK...`) 
* 不要理会首个字符 `/`. 有或者没有都是可以的。
* ApkEditor 无法作用到下面的这些文件。（因为这些文件在apk中是必不可少的）

	* /META-INF/**
	* resources.arsc
	* AndroidManifest.xml

* 与Android Gradle DSL 中的 [PackagingOptions][2] 不同

	* 经过测试PackagingOptions无法对这些资源产生影响 `/res/  /assets/  .classes`
	* 它的设置规则在 flavor和buildType中很不友好。

联系
---------

* 获得源码 [GITHUB][3].
* 访问我的个人博客 [马培羽][4]
* 邮件 [mason.mpy@gmail.com](mason.mpy@gmail.com)

License
=======

    Copyright 2017 Mapeiyu

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


 [1]: https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getPathMatcher-java.lang.String-
 [2]: http://google.github.io/android-gradle-dsl/current/com.android.build.gradle.internal.dsl.PackagingOptions.html
 [3]: https://github.com/masonTool/ApkEditor
 [4]: http://www.mapeiyu.com
