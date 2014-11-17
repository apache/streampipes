/**
 * @author Florian Kaulfersch
 */

function getTextInputForm(label, placeholder, i, value){
	var string = "<div class='form-group'>";
	string+= "<label class='col-md-4 control-label' for='textinput"+i+"'>"+label+"</label>";
	string+="<div class='col-md-4'>";
	string+="<input id='textinput"+i+"' name='"+label+"' value='"+value+"' placeholder='"+placeholder+"' class='form-control input-md' required='' type='text'>";
	string+="</div></div>";
	return string;
}

function getRadioInputForm(label, radioButtonArray, i){
	var string="<div class='form-group'>";
	string+= "<label class='col-md-4 control-label' for='radios"+i+"'>"+label+"</label>";
	string+= "<div class='col-md-4'>";
	for (var j = 0; j< radioButtonArray.length ; j++){
		string+=" <div class='radio'>";
		string+="<label for='radios-"+i+"-"+j+"'>";
		if (radioButtonArray[j].selected == true){
			string +="<input name='"+label+"' id='radios-"+i+"-"+j+"' checked="+true+" value='"+radioButtonArray[j].humanDescription+"' type='radio'>";
		}else{
			string +="<input name='"+label+"' id='radios-"+i+"-"+j+"' value='"+radioButtonArray[j].humanDescription+"' type='radio'>";
		}
		string += radioButtonArray[j].humanDescription;
		string += "</label></div>";
	}
	string += "</div></div>";
	return string;
}

function getSelectInputForm(label, selectArray, i){
	
	var string ="<div class='form-group'>";
	string += "<label class='col-md-4 control-label' for='selectbasic"+i+"'>"+label+"</label>";
	string += "<div class='col-md-4'>";
	string += "<select id='selectbasic"+i+"' name='"+label+"' class='form-control'>";
	for (var j = 0; j < selectArray.length; j++){
		if (selectArray[j].selected == true){
			string += "<option selected='"+true+"' value='"+selectArray[j].humanDescription+"'>"+selectArray[j].humanDescription+"</option>";
		}else{
			string += "<option value='"+selectArray[j].humanDescription+"'>"+selectArray[j].humanDescription+"</option>";
		}
	}
	string += "</select></div></div>";
	return string;
}

/*
<!-- Multiple Radios -->
<div class="form-group">
  <label class="col-md-4 control-label" for="radios">Multiple Radios</label>
  <div class="col-md-4">
  <div class="radio">
    <label for="radios-0">
      <input name="radios" id="radios-0" value="s" checked="checked" type="radio">
      sec(s)
    </label>
	</div>
  <div class="radio">
    <label for="radios-1">
      <input name="radios" id="radios-1" value="m" type="radio">
      min(s)
    </label>
	</div>
  <div class="radio">
    <label for="radios-2">
      <input name="radios" id="radios-2" value="h" type="radio">
      hour(s)
    </label>
	</div>
  </div>
</div>


<!-- Select Basic -->
<div class="form-group">
  <label class="col-md-4 control-label" for="selectbasic">Select Basic</label>
  <div class="col-md-4">
    <select id="selectbasic" name="selectbasic" class="form-control">
      <option value="1">Option one</option>
      <option value="2">Option two</option>
    </select>
  </div>
</div>





*/