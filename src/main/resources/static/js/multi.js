function addItem(buttonComponent) {
    var id = $(buttonComponent).attr("data");
    var inputElement = $('.tag_value_input_' + id);
    addItemById(id, inputElement.val());
    inputElement.val("");

}

function addItemById(id, value, updateValue=true) {

    if (value.trim() === ""){
        return;
    }
    var valuesElement = $('.values_' + id);
    var hiddenValue = $('#' + id);


    if (updateValue) {
        if (hiddenValue.val() !== "") {
            hiddenValue.val(hiddenValue.val() + ";");
        }
        hiddenValue.val(hiddenValue.val()  + value);
    }


    var div = $(document.createElement("span"));
    valuesElement.append(div);
    var valueSpan = $(document.createElement("span"));
    valueSpan.addClass("multi_value_text_" + id);
    valueSpan.addClass("multi_value_text");
    var removeSpan = $(document.createElement("span"));
    removeSpan.addClass("multi-remove-btn");
    removeSpan.click ( function (e){ removeMultiValue(id, div)});

    valueSpan.html(value);
    div.append(valueSpan);
    div.append(removeSpan);
    valuesElement.append(div);

}

function removeMultiValue(id, parent) {
    parent.remove();

    syncWith(id)
}

function syncWith(id) {
    var hiddenValue = $('#' + id);
    var result = "";

    $( ".multi_value_text_" + id ).each( function( index, element ){
        if (result !== ""){
            result += ";";
        }
        result += $(element).html() ;
    });

    hiddenValue.val(result)

}

function syncAvers(id) {
    var hiddenValue = $('#' + id).val().split(";");
    for (var i = 0; i < hiddenValue.length; i ++){
        addItemById(id, hiddenValue[i], false)
    }
}