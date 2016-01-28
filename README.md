[ ![Download](https://api.bintray.com/packages/moreno/maven/MapViewPager/images/download.svg) ](https://bintray.com/moreno/maven/MapViewPager/_latestVersion)
# MapViewPager

Android library that connects ViewPager fragments with Google Maps markers and makes them work together.

## Download

#### Gradle

```
repositories {
    maven {
        url  "http://dl.bintray.com/moreno/maven" 
    }
}

dependencies {
    compile 'com.github.nitrico.mapviewpager:mapviewpager:0.0.1'
}
```

## Usage

#### Include it in your layout

```xml
<com.github.nitrico.mapviewpager.MapViewPager
        android:id="@+id/mapViewPager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:viewPagerWeight="1"
        app:mapWeight="1"
        app:mapGravity="1"
        app:mapOffset="56dp" />
```

#### Advanced usage

If you want more personalization you can use the MapViewPager.Builder()

Check the examples in the app folder!

## License
```
Copyright 2016 Miguel Ángel Moreno

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
