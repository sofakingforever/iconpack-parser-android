IconPackParser-Android
======

An Android Library to parse Icon Packs installed on the device, and fetch their icons.

Download
--------

using Gradle:
```groovy

repositories {

    maven { url "http://dl.bintray.com/sofakingforever/libraries" }

}

dependencies {
    compile 'com.sofakingforever.libraries:iconpack:0.0.8@aar'
}
```


Usage Example
--------

with Java:

```java

mIconPackManager = new IconPackManager();

mIconPackManager.loadInstalledIconPacksAsync(getApplicationContext(), new IconPackManager.Listener() {
            @Override
            public void onIconPacksLoaded() {

                mIconPack = mIconPackManager.getInstalledIconPack("com.example.iconpack");

                mIconPack.initAppFilterAsync(true, new IconPack.AppFilterListener() {
                    @Override
                    public void onAppFilterLoaded() {

                        ComponentName component = new ComponentName("com.app.example", ".ExampleActivity");

                        Drawable icon = mIconPack.getDefaultIconForPackage(MainActivity.this, component, true);


                    }

                    @Override
                    public void onLoadingFailed(Exception e) {

                    }
                });


            }
        });

```


Still on the Todo List
--------
- Icon Selection Activity
- IconPack Selection Activity


### Used in
--------
- [Dailydo Android Launcher](https://play.google.com/store/apps/details?id=com.sofaking.dailydo)

License
-------

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
