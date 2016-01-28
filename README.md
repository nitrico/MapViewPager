[ ![Download](https://api.bintray.com/packages/moreno/maven/MapViewPager/images/download.svg) ](https://bintray.com/moreno/maven/MapViewPager/_latestVersion)

# MapViewPager

Android library that connects ViewPager fragments with Google Maps markers and makes them work together.


## Features

- [x] Support for more than 1 marker per fragment
- [x] Default position for fragments with more than one marker automatically calculated


## Download

#### Gradle

```gradle
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

##### For 1 (or 0) markers per fragment:

Create a ViewPager adapter extending from `MapViewPager.Adapter` and override method
`public CameraPosition getCameraPosition(int position)` returning the marker camera position for fragment each fragment.

You can create it like that `CameraPosition.builder().target(new LatLng(latitude, longitude)).zoom(zoom).build();`

Include the view in your xml layout

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
and call `mapViewPager.start(this, adapter, callback /*optional*/);` passing the AppCompatActivity (or FragmentActivity) and adapter instances. You can also pass a MapViewPager.Callback instance to get notified when the GoogleMap object is created and working.

##### XML attributes

|Attribute|Format|Default value|Description
|---|---|---|---|
|viewPagerWeight|integer|1||
|mapWeight|integer|1||
|mapGravity|integer|1||
|mapOffset|dimension|0dp||
|mapPaddingLeft|dimension|0dp||
|mapPaddingTop|dimension|0dp||
|mapPaddingRight|dimension|0dp||
|mapPaddingBottom|dimension|0dp||
|markersAlpha|float (0..1)|0.4||


#### Advanced usage

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
