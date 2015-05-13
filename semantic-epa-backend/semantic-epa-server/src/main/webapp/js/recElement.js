function recElement(json){
    this.json = json;
    this.getjQueryElement = function(){
        return $('<a>')
            .data("recObject", this)
            .append($('<span>')
            .addClass("recommended-item")
            .append($('<img>').attr("src", this.json.iconUrl).addClass("recommended-item-img")));

    };


}
