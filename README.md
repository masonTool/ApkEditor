ApkEditor
========

[忍受不了我的蹩脚英文， 就看中文版吧](README_CH.md)

A gradle pulgin for Android Application project. When you import many dependency libs or aars, some redundant resources make your apk pretty huge. 
ApkEditor can exclude resources from APK, aim to reduce the APK's volume as more as possible.

Though, it needs your ability of distinguishing the resources which is useful in runtime  or not. In bad case, your program crashed.

Download & Use
--------

1. ApkEditor checks the android application's environment. So make sure the android plugin `com.android.application` have been applied.

2. Apply to your project:

	```
	buildscript {
	    repositories {
	        jcenter()
	    }
	    dependencies {
	        classpath 'com.mapeiyu.apkeditor:apkeditor:1.0.3'
	    }
	}
	apply plugin: 'apkeditor'
	```
3. Use the `apkeditor` scope:

	```
	apkeditor {
        exclude '/assets/**/sb/*.so'

        release {
            exclude 'lib/armeabi/**'
        }
        //suppose you have a flavor named black
        blackDebug {
            exclude '/res/layout/**'
            exclude 'res/drawable*/*.xml'
        }
    }
	```
4. Then build or install as normal. The Apk generated will packaged without above rousources which match the rules.

Explanation
----------

* The `exclude` varient in the root scope apply to all the buildType and flavors.
* The `exclude` varient under the param as 'release' 'debug' 'backDebug', appliy to specified apk.
* The `exclude` varient can be execute multi times.
* The rule of `exclude` value folows the convention of [Java Filesystem API][1], and it's come from the APK's file system. (You could get it by unzip the apk, or from the android studio `Build->Analyze APK...`) 
* Don't care about the first `/`. All the rule is OK.
* ApkEditor can't effect on below files which is essential

	* /META-INF/**
	* resources.arsc
	* AndroidManifest.xml

* Different from the Android Plugin of scope [PackagingOptions][2]

	* As tested PackagingOptions do not effect on '/res/', '/assets/', '.classes'
	* It's rule is not so friendly with multi flavors

Contacts
---------

* You can get the sources from the [GITHUB][3].
* Visit my personal blog [马培羽][4]
* Email [mason.mpy@gmail.com](mason.mpy@gmail.com)

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
