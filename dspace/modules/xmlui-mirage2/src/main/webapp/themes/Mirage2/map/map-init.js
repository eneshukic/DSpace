proj4.defs("EPSG:31276", "+proj=tmerc +lat_0=0 +lon_0=18 +k=0.9999 +x_0=6500000 +y_0=0 +ellps=bessel +towgs84=472.8677,187.8769,544.7084,5.76198422,5.3222842,-12.80666941,1.54517287 +units=m");


var map;

var style = new ol.style.Style({
    fill: new ol.style.Fill({
        color: 'rgba(255, 255, 255, 0.6)'
    }),
    stroke: new ol.style.Stroke({
        color: '#c00000',
        width: 2
    }),
    image: new ol.style.Circle({
        radius: 6,
        fill: new ol.style.Fill({
            color: '#c00000'
        })
    })
});

var source = new ol.source.Vector({wrapX: false});

var vector = new ol.layer.Vector({
    source: source,
    style: style
});

var draw;
//document.getElementById("aspect_submission_StepTransformer_field_da_Municipality_Name").addEventListener("change",changeCM);
//get query strings
function getQueryStrings() {
    var assoc  = {};
    var decode = function (s) { return decodeURIComponent(s.replace(/\+/g, " ")); };
    var queryString = location.search.substring(1);
    var keyValues = queryString.split('&');

    for(var i in keyValues) {
        var key = keyValues[i].split('=');
        if (key.length > 1) {
            assoc[decode(key[0])] = decode(key[1]);
        }
    }

    return assoc;
}

function handleSearch(extent) {
    var reprojectedExtent = ol.proj.transformExtent(extent, 'EPSG:3857', 'EPSG:31276');
    //document.getElementById(fieldID).value = reprojectedExtent[0] + "," + reprojectedExtent[2] + "," + reprojectedExtent[1] + "," + reprojectedExtent[3];
    document.getElementById(fieldID).value = Math.round(reprojectedExtent[0]) + " " + Math.round(reprojectedExtent[1]) + " " + Math.round(reprojectedExtent[2]) + " " + Math.round(reprojectedExtent[3]);

    document.getElementById("spatial-index").value = "spatial-search";

    document.getElementById(fieldID).parentNode.classList.remove('has-error');
}

function handleSubmit(extent) {
    var reprojectedExtent = ol.proj.transformExtent(extent, 'EPSG:3857', 'EPSG:31276');
    document.getElementById(fieldID + '_west').value = Math.round(reprojectedExtent[0]);
    document.getElementById(fieldID + '_east').value = Math.round(reprojectedExtent[2]);
    document.getElementById(fieldID + '_south').value = Math.round(reprojectedExtent[1]);
    document.getElementById(fieldID + '_north').value = Math.round(reprojectedExtent[3]);
}

// Add Map function with the proper layers and controls
function addMap(mapID, purpose) {
    var raster = new ol.layer.Tile({
        source: new ol.source.OSM()
    });

    map = new ol.Map({
        layers: [raster, vector],
        target: mapID,
        view: new ol.View({
            center: [2011963.79, 5445901.91],
            zoom: 6,
            minZoom: 6
        })
    });

    if (purpose == 'submit' || purpose == 'search') {
        draw = new ol.interaction.Draw({
            source: source,
            style: style,
            type: ('Circle'),
            geometryFunction: ol.interaction.Draw.createBox()
        });

        draw.on('drawstart', function (e) {
            source.clear();
        });

        draw.on('drawend', function (e) {
            var extent = e.feature.getGeometry().getExtent();
            if (purpose == 'search') {
                handleSearch(extent);
            } else if (purpose == 'submit') {
                handleSubmit(extent);
            }
        });

        map.addInteraction(draw);
    }

    if (purpose == 'search') {
        var isInputValidCoordinates = function(spitedValues) {
            //must be 4 coordinates
            if (spitedValues.length === 4) {
                //in 31276 every coordinate is 7 chars long
                if (spitedValues[0].length === 7 &&
                    spitedValues[1].length === 7 &&
                    spitedValues[2].length === 7 &&
                    spitedValues[3].length === 7 &&
                    parseInt(spitedValues[0]) == spitedValues[0] &&
                    parseInt(spitedValues[1]) == spitedValues[1] &&
                    parseInt(spitedValues[2]) == spitedValues[2] &&
                    parseInt(spitedValues[3]) == spitedValues[3]) {
                    return true;
                }
            }
            return false;
        };

        var searchDiv = document.getElementById("spatial-search-div");
        if (searchDiv) {
            var parentElement = searchDiv.parentNode;
            if (parentElement.nodeName === 'FORM') {
                parentElement.addEventListener("submit", function() {
                    var inputValue = document.getElementById(fieldID).value;
                    var spitedValues = inputValue.split(' ');
                    if (isInputValidCoordinates(spitedValues)) {
                        document.getElementById("spatial-index").value = "spatial-search";
                    } else {
                        document.getElementById("spatial-index").value = "";
                    }
                });
            }
        }
        var spatialQueryFields = document.getElementsByName('spatial-query');
        if (spatialQueryFields.length === 1) {
            var timerId;
            var spatialQueryField = spatialQueryFields[0];
            var drawBox = function() {
                var inputValue = document.getElementById(fieldID).value;
                var spitedValues = inputValue.split(' ');
                source.clear();
                if (isInputValidCoordinates(spitedValues)) {
                    addItemsBoxToMap(spitedValues[0], spitedValues[1],
                        spitedValues[2], spitedValues[3]);
                    spatialQueryField.parentNode.classList.remove('has-error');
                } else {
                    spatialQueryField.parentNode.classList.add('has-error');
                }
            };
            spatialQueryField.addEventListener('change', function () {
                if (timerId) {
                    clearTimeout(timerId);
                }
                timerId = setTimeout(drawBox, 300);
            });
        }

    }
}

// Adds an Item's Bounding Box to map and zooms on it
function addItemsBoxToMap(west, south, east, north) {
    var reprojectedExtent = ol.proj.transformExtent([west, south, east, north], 'EPSG:31276', 'EPSG:3857');
    var polygon = ol.geom.Polygon.fromExtent(reprojectedExtent);
    var feature = new ol.Feature({
        geometry: polygon
    });
    source.addFeature(feature);
    map.getView().fit(polygon, {
        size: map.getSize(),
        nearest: false
    });
}

// Draws Bounding Box on Map based when user enter coordinates manually on text inputs during submission
function updateMap(id) {
    source.clear();
    var fid = id.substring(0, id.lastIndexOf('_'));
    addItemsBoxToMap(document.getElementById(fid + '_west').value, document.getElementById(fid + '_south').value,
        document.getElementById(fid + '_east').value, document.getElementById(fid + '_north').value);
}

// Clears spatial-query hidden input value an removes Box from map
function clearMap() {
    document.getElementById(fieldID).value = "";
    document.getElementById("spatial-index").value = "";
    source.clear();
}

//add filter to cadaster municipality based on municipality name
function changeCM(id){
    source.clear();
    if (id == "aspect_submission_StepTransformer_field_da_Municipality_Name"){
        var cmexists = document.getElementById("aspect_submission_StepTransformer_field_da_CadastralMunicipality_Name");
        var cmoldexists = document.getElementById("aspect_submission_StepTransformer_field_da_CadastralMunicipalitySP_Name");
        if (cmexists != null){
            document.getElementById("aspect_submission_StepTransformer_field_da_CadastralMunicipality_Name").value = document.getElementById(id).value;
        }
        if (cmoldexists != null){
            document.getElementById("aspect_submission_StepTransformer_field_da_CadastralMunicipalitySP_Name").value = document.getElementById(id).value;
        }
    }

}

/*
 function changeCM(){
 //source.clear();
 var cmexists = document.getElementById("aspect_submission_StepTransformer_field_da_CadastralMunicipality_Name");
 var cmoldexists = document.getElementById("aspect_submission_StepTransformer_field_da_CadastralMunicipality_NameSP");
 if (cmexists != null){
 cmexists.value = document.getElementById(id).value;
 }
 if (cmoldexists != null){
 cmoldexists.value = document.getElementById(id).value;
 }

 }
 */

function hideSpatialSearch(){
    document.getElementById("spatial-search-div").style.display = "none";
    document.getElementById("hideSpatialSearch").style.display = "none";
    document.getElementById("showSpatialSearch").style.display = "inline";
}

function showSpatialSearch(){
    document.getElementById("spatial-search-div").style.display = "block";
    document.getElementById("hideSpatialSearch").style.display = "inline";
    document.getElementById("showSpatialSearch").style.display = "none";
}

document.addEventListener("DOMContentLoaded", function (event) {
    var loadJS = function (url, location) {
        //url is URL of external file, implementationCode is the code
        //to be called from the file, location is the location to
        //insert the <script> element

        var scriptTag = document.createElement('script');
        scriptTag.src = url;

        location.appendChild(scriptTag);
    };
    loadJS('/xmlui/static/filter/js/jquery.csv.min.js', document.body);
    loadJS('/xmlui/static/filter/js/js.cookie.min.js', document.body);

    var $areasGroup = $("#areasGroup");
    $areasGroup.empty();

    var $areas = $("#areas");
    $areas.empty();

    $('.modal').on('show.bs.modal', function () {
        var dialog = this;
        $.ajax({
            url: "/xmlui/static/filter/filter.csv",
            beforeSend: function (xhr) {
                xhr.overrideMimeType("text/plain; charset=UTF-8");
            },
            async: true,
            cache: false,
            success: function (csvd) {
                var data = $.csv.toArrays(csvd);
                var groups = [];
                var i = 0;
                data.splice(0, 1);
                for (i = 0; i < data.length; i++) {
                    var group = data[i][0];
                    if (groups.indexOf(group) === -1) {
                        var index = groups.length;
                        groups.push(group);
                        data[i][0] = index;
                    } else {
                        data[i][0] = groups.indexOf(group);
                    }
                }
                $areasGroup.empty();
                var preselectGroup = Cookies.get('bboxGroup');
                for (i = 0; i < groups.length; i++) {
                    var newGroupItem = $("<option />").val(i).text(groups[i]);
                    if (preselectGroup !== undefined && preselectGroup === groups[i]) {
                        newGroupItem.prop('selected', true);
                    }
                    $areasGroup.append(newGroupItem);
                }
                $areas.empty();
                data.sort(function (a, b) {
                    if (a[1] === b[1]) {
                        return 0;
                    } else {
                        return (a[1] < b[1]) ? -1 : 1;
                    }
                });
                var preselectItem = Cookies.get('bboxItem');
                for (i = 0; i < data.length; i++) {
                    var newItem = $("<option />").val(data[i][0])
                        .text(data[i][1])
                        .attr("ll", data[i][2])
                        .attr("lb", data[i][3])
                        .attr("ur", data[i][4])
                        .attr("ut", data[i][5]);
                    if (preselectItem !== undefined && preselectItem === data[i][1]) {
                        newItem.prop('selected', true);
                    }
                    $areas.append(newItem);
                }

                $areasGroup.off('change');
                var $options = $areas.find('option');

                $areasGroup.on('change', function () {
                    var filtered = $options.filter('[value="' + this.value + '"]');
                    $areas.html(filtered);
                    if (filtered.length) {
                        $areas.find(":first-child").prop('selected', true);
                        if (preselectItem !== undefined) {
                            $areas.children(":contains(" + preselectItem + ")").prop('selected', true);
                        }
                    }
                }).trigger('change');

                var selectBtn = $("#selectArea");
                selectBtn.off('click');
                selectBtn.on('click', function () {
                    var selectedItem = $areas.find(":selected");
                    var ll = selectedItem.attr('ll');
                    var lb = selectedItem.attr('lb');
                    var ur = selectedItem.attr('ur');
                    var ut = selectedItem.attr('ut');
                    Cookies.set('bboxItem', selectedItem.text(), { expires: 7 });
                    Cookies.set('bboxGroup', $areasGroup.find(":selected").text(), { expires: 7 });
                    var element = document.getElementById(fieldID);
                    element.value = ll + " " + lb + " " + ur + " " + ut;
                    if ("createEvent" in document) {
                        var evt = document.createEvent("HTMLEvents");
                        evt.initEvent("change", false, true);
                        element.dispatchEvent(evt);
                    } else {
                        element.fireEvent("onchange");
                    }
                });
            },
            dataType: "text"
        });
    });

    $('body').append($('#selectBBox'));

});
