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
            zoom: 6
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
}

// Adds an Item's Bounding Box to map and zooms on it
function addItemsBoxToMap(west, south, east, north) {
    var reprojectedExtent = ol.proj.transformExtent([west, south, east, north], 'EPSG:31276', 'EPSG:3857');
    var polygon = ol.geom.Polygon.fromExtent(reprojectedExtent);
    var feature = new ol.Feature({
        geometry: polygon
    });
    source.addFeature(feature);
    map.getView().fit(polygon, map.getSize())
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
