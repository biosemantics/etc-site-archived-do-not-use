function barchart(div, data) {

	var x = d3.scale.linear().domain([ 0, d3.max(data) ]).range(
			[ "0px", "420px" ]);

	var chart = d3.select(div).append("div").attr("class", "chart");

	chart.selectAll("div").data(data).enter().append("div").style("width", x)
			.text(function(d) {
				return d;
			});

}

function force_directed_graph(div, nodes, links) {
	alert(JSON.stringify(nodes, null, 2));
	alert(JSON.stringify(links, null, 2));
	var width = 960, height = 500;

	var color = d3.scale.category20();

	var force = d3.layout.force().linkDistance(200).charge(-120).linkStrength(2).size(
			[ width, height ]);

	var svg = d3.select(div).append("svg").attr("width", width).attr(
			"height", height);

	var newnodes = nodes.slice(), newlinks = [], bilinks = [];

	links.forEach(function(link) {
		var s = newnodes[link.source], t = newnodes[link.target], i = {}; // intermediate node
		newnodes.push(i);
		newlinks.push({
			source : s,
			target : i
		}, {
			source : i,
			target : t
		});
		bilinks.push([ s, i, t ]);
	});

	force.nodes(newnodes).links(newlinks).start();

	var link = svg.selectAll(".link").data(bilinks).enter().append("path")
			.attr("class", "link");

	/*var node = svg.selectAll(".node").data(nodes).enter()
			.append("circle").attr("class", "node").attr("r", 5).style("fill",
					function(d) {
						return color(d.group);
					}).call(force.drag);*/
	
	var gnodes = svg.selectAll(".node").data(nodes).enter().append("g").classed("gnode", true);

	var node = gnodes.append("circle").attr("class", "node").attr("r", 20)
			.style("fill", function(d) {
				//alert(d.group);
				//alert(color(d.group));
				return color(d.group);
			}).call(force.drag);

	var labels = gnodes.append("text").text(function(d) {
		return d.name;
	});

	node.append("title").text(function(d) {
		return d.name;
	});

	force.on("tick", function() {
		link.attr("d", function(d) {
			return "M" + d[0].x + "," + d[0].y + "S" + d[1].x + "," + d[1].y
					+ " " + d[2].x + "," + d[2].y;
		});
		gnodes.attr("transform", function(d) {
			return "translate(" + d.x + "," + d.y + ")";
		});
	});
}


function force_directed_graph2(div, nodes, links) {
	//alert(JSON.stringify(nodes, null, 2));
	//alert(JSON.stringify(links, null, 2));
	
	// Compute the distinct nodes from the links.
	links.forEach(function(link) {
	  link.source = nodes[link.source] || (nodes[link.source] = {name: link.source});
	  link.target = nodes[link.target] || (nodes[link.target] = {name: link.target});
	});
	
	var color = d3.scale.category20();

	var width = div.offsetWidth - 50,
	   height = div.offsetHeight - 50;
	//alert(width);
	//alert(height);

	var force = d3.layout.force()
	    .nodes(d3.values(nodes))
	    .links(links)
	    .size([width, height])
	    .linkDistance(60)
	    .charge(-300)
	    .on("tick", tick)
	    .start();

	var svg = d3.select(div).append("svg").attr("width", width).attr("height", height);

	// Per-type markers, as they don't inherit styles.
	svg.append("defs").selectAll("marker")
	    .data(["superclass", "partof", "synonym"])
	  .enter().append("marker")
	    .attr("id", function(d) { return d; })
	    .attr("viewBox", "0 -5 10 10")
	    .attr("refX", 15)
	    .attr("refY", -1.5)
	    .attr("markerWidth", 6)
	    .attr("markerHeight", 6)
	    .attr("orient", "auto")
	  .append("path")
	    .attr("d", "M0,-5L10,0L0,5");

	var path = svg.append("g").selectAll("path")
	    .data(force.links())
	  .enter().append("path")
	    .attr("class", function(d) { return "link " + d.type; })
	    .attr("marker-end", function(d) { return "url(#" + d.type + ")"; });

	var circle = svg.append("g").selectAll("circle")
	    .data(force.nodes())
	  .enter().append("circle")
	    .attr("r", 6)
	    .style("fill", function(d) {
	    	return color(d.group);
	    })
	    .call(force.drag);

	var text = svg.append("g").selectAll("text")
	    .data(force.nodes())
	  .enter().append("text")
	    .attr("x", 8)
	    .attr("y", ".31em")
	    .text(function(d) { return d.name; });

	// Use elliptical arc path segments to doubly-encode directionality.
	function tick() {
	  path.attr("d", linkArc);
	  circle.attr("transform", transform);
	  text.attr("transform", transform);
	}

	function linkArc(d) {
	  var dx = d.target.x - d.source.x,
	      dy = d.target.y - d.source.y,
	      dr = Math.sqrt(dx * dx + dy * dy);
	  return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
	}

	function transform(d) {
	  return "translate(" + d.x + "," + d.y + ")";
	}
	
	
	//d3.select(window).on('resize', resize); 

	//function resize() {
	//	alert("resize");
		// do the actual resize...
	//}
}