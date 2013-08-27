var menuHighlightLayer = document.getElementById("menuHighlightLayer");

var menuMatrixGeneration = document.getElementById("menuMatrixGeneration");
if(menuMatrixGeneration != null) {
	menuMatrixGeneration.addEventListener("mouseover", menuMatrixGenerationSelect, false);
	menuMatrixGeneration.addEventListener("mouseout", menuDeselect, false);
}

var menuTreeGeneration = document.getElementById("menuTreeGeneration");
if(menuTreeGeneration != null) {
	menuTreeGeneration.addEventListener("mouseover", menuTreeGenerationSelect, false);
	menuTreeGeneration.addEventListener("mouseout", menuDeselect, false);
}

var menuTaxonomyComparison = document.getElementById("menuTaxonomyComparison");
if(menuTaxonomyComparison != null) {
	menuTaxonomyComparison.addEventListener("mouseover", menuTaxonomyComparisonSelect, false);
	menuTaxonomyComparison.addEventListener("mouseout", menuDeselect, false);
}

var menuVisualization = document.getElementById("menuVisualization");
if(menuVisualization != null) { 
	menuVisualization.addEventListener("mouseover", menuVisualizationSelect, false);
	menuVisualization.addEventListener("mouseout", menuDeselect, false);
}

function menuMatrixGenerationSelect() { 
	var higlightLayerWidth = menuHighlightLayer.offsetWidth; //cannot be done globbally, window could have been resized
	var top = 0; 
	var bottom = menuHighlightLayer.offsetHeight;
	var start = (higlightLayerWidth / 2) - 400; 
	var end = (higlightLayerWidth / 2) - 400 + 200;
	menuHighlightLayer.style.clip = 'rect(' + top + 'px, ' + end + 'px, ' + bottom + 'px, ' + start + 'px)';
}

function menuTreeGenerationSelect() { 
	var higlightLayerWidth = menuHighlightLayer.offsetWidth; //cannot be done globbally, window could have been resized
	var top = 0; 
	var bottom = menuHighlightLayer.offsetHeight;
	var start = (higlightLayerWidth / 2) - 200; 
	var end = (higlightLayerWidth / 2) - 200 + 200;
	menuHighlightLayer.style.clip = 'rect(' + top + ', ' + end + ', ' + bottom + ', ' + start + ')';
}

function menuTaxonomyComparisonSelect() { 
	var higlightLayerWidth = menuHighlightLayer.offsetWidth; //cannot be done globbally, window could have been resized
	var top = 0; 
	var bottom = menuHighlightLayer.offsetHeight;
	var start = (higlightLayerWidth / 2) + 200 - 200; 
	var end = (higlightLayerWidth / 2) + 240;
	menuHighlightLayer.style.clip = 'rect(' + top + ', ' + end + ', ' + bottom + ', ' + start + ')';
}

function menuVisualizationSelect() { 
	var higlightLayerWidth = menuHighlightLayer.offsetWidth; //cannot be done globbally, window could have been resized
	var top = 0; 
	var bottom = menuHighlightLayer.offsetHeight;
	var start = (higlightLayerWidth / 2) + 440 - 200; 
	var end = (higlightLayerWidth / 2) + 420;
	menuHighlightLayer.style.clip = 'rect(' + top + ', ' + end + ', ' + bottom + ', ' + start + ')';
}

function menuDeselect() {  
	menuHighlightLayer.style.clip = 'rect(0, 0, 0, 0)';
}

/*
function login() {
	var user = document.getElementById('userField').value;
	var password = document.getElementById('passwordField').value;
	if(user=='user' && password =='password') {
		window.location = 'loggedIn.html';
	} else {
		alert("Wrong credentials");
	}
}
*/