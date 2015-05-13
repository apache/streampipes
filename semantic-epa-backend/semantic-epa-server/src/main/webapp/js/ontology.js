
function generateTree(data, destination) {
	$('#' +destination).jstree({ 'core' : {
	    'data' : data
	},
	"plugins" : [ "contextmenu" ]});
	
	$("#" +destination).on("select_node.jstree",
     function(evt, data){
         if (destination == "typeHierarchy") getNodeDetailsType(data.node.id);
     }
);
}

function getTreeData(type, destination) {
	
	$.ajax({
        dataType: "json",
        url: type,
        success: function (data) {
            generateTree(data, destination);
        },
        error: function (data) {
            displayErrors(data);
        }
    });
}

function getNodeDetailsType(destinationUri) {
	$.ajax({
        dataType: "json",
        url: standardUrl +"ontology/types/" +encodeURIComponent(destinationUri),
        success: function (data) {
            showNodeDetails(data);
        },
        error: function (data) {
            displayErrors(data);
        }
    });
}

function showNodeDetails(data) {
	$("#typeHierarchyDetails").empty();
	$("#typeHierarchyDetails").append("<form class='form-horizontal'>");
	$.each( data.statements, function( index, value ) {
		$("#typeHierarchyDetails").append(buildForm(this));
	});
	$("#typeHierarchyDetails").append("</form>");
}

function buildForm(data) {
	return "<div class='row'>"
		+"<div class='col-md-12'>" 
		+"<div class='form-group'>"
		+"<label for='form" +data.predicate +"'>Predicate </label>"
		+"<input type='text' class='form-control' id='form" +data.predicate +"' style='width:80%' value='" +data.predicate +"'>"
		+"</div></div>"
		+"<div class='col-md-12'>" 
		+"<div class='form-group'>"
		+"<label for='form" +data.object +"'>Object </label>"
		+"<input type='text' class='form-control' id='form" +data.object +"' style='width:80%' value='" +data.object +"'></div></div></div>"
		+"<hr/>";
}

