window.TAB = "    ";
function IsArray(obj) {
    return obj &&
        typeof obj === 'object' &&  typeof obj.length === 'number' && !(obj.propertyIsEnumerable('length'));
}

function Process(continner,json) {
    document.getElementById(continner).style.display = "block";
    if (json == "") {
        json = '""';
    }
    var obj = eval("[" + json + "]");
    var html = ProcessObject(obj[0], 0, false, false, false);
    document.getElementById(continner).innerHTML =html;

}

function ProcessObject(obj, indent, addComma, isArray, isPropertyContent) {
    var html = "";
    var comma = (addComma) ? "<span class='Comma'>,</span> ": "";
    var type = typeof obj;
    if (IsArray(obj)) {
        if (obj.length == 0) {
            html += GetRow(indent, "<span class='ArrayBrace'>[ ]</span>" + comma, isPropertyContent);
        } else {
            html += GetRow(indent, "<span class='ArrayBrace'>[</span>", isPropertyContent);
            for (var i = 0; i < obj.length; i++) {
                html += ProcessObject(obj[i], indent + 1, i < (obj.length - 1), true, false);
            }
            html += GetRow(indent, "<span class='ArrayBrace'>]</span>" + comma);
        }
    } else {
        if (type == "object" && obj == null) {
            html += FormatLiteral("null", "", comma, indent, isArray, "Null");
        } else {
            if (type == "object") {
                var numProps = 0;
                for (var prop in obj) {
                    numProps++;
                }
                if (numProps == 0) {
                    html += GetRow(indent, "<span class='ObjectBrace'>{ }</span>" + comma, isPropertyContent)
                } else {
                    html += GetRow(indent, "<span class='ObjectBrace'>{</span>", isPropertyContent);
                    var j = 0;
                    for (var prop in obj) {
                        html += GetRow(indent + 1, '<span class="PropertyName">"' + prop + '"</span>: ' + ProcessObject(obj[prop], indent + 1, ++j < numProps, false, true))
                    }
                    html += GetRow(indent, "<span class='ObjectBrace'>}</span>" + comma);
                }
            } else {
                if (type == "number") {
                    html += FormatLiteral(obj, "", comma, indent, isArray, "Number");
                } else {
                    if (type == "boolean") {
                        html += FormatLiteral(obj, "", comma, indent, isArray, "Boolean");
                    } else {
                        if (type == "function") {
                            obj = FormatFunction(indent, obj);
                            html += FormatLiteral(obj, "", comma, indent, isArray, "Function");
                        } else {
                            if (type == "undefined") {
                                html += FormatLiteral("undefined", "", comma, indent, isArray, "Null");
                            } else {
                                html += FormatLiteral(obj, '"', comma, indent, isArray, "String");
                            }
                        }
                    }
                }
            }
        }
    }
    return html;
}

function FormatLiteral(literal, quote, comma, indent, isArray, style) {
    if (typeof literal == "string") {
        literal = literal.split("<").join("&lt;").split(">").join("&gt;");
    }
    var str = "<span class='" + style + "'>" + quote + literal + quote + comma + "</span>";
    if (isArray) {
        str = GetRow(indent, str);
    }
    return str;
}

function FormatFunction(indent, obj) {
    var tabs = "";
    for (var i = 0; i < indent; i++) {
        tabs += window.TAB;
    }
    var funcStrArray = obj.toString().split("\n");
    var str = "";
    for (var i = 0; i < funcStrArray.length; i++) {
        str += ((i == 0) ? "": tabs) + funcStrArray[i] + "\n";
    }
    return str;
}

function GetRow(indent, data, isPropertyContent) {
    var tabs = "";
    for (var i = 0; i < indent && !isPropertyContent; i++) {
        tabs += window.TAB;
    }
    if (data != null && data.length > 0 && data.charAt(data.length - 1) != "\n") {
        data = data + "\n";
    }
    return tabs + data;
}

var isXML = function(elem){
    var documentElement = (elem ? elem.ownerDocument || elem : 0).documentElement;
    return documentElement ? documentElement.nodeName !== "HTML" : false;
}

function formatXML(continner,content) {
    var xml_doc = (new DOMParser()).parseFromString(content.replace(/[\n\r]/g, ""), 'text/xml');

    function build_xml(index, list, element) {
        var t = [];
        for (var i = 0; i < index; i++) {
            t.push('&nbsp;&nbsp;&nbsp;&nbsp;');
        }
        t = t.join("");
        list.push(t + '&lt;<span class="code-key">'+ element.nodeName +'</span>&gt;\n');
        for (var i = 0; i < element.childNodes.length; i++) {
            var nodeName = element.childNodes[i].nodeName;
            if (element.childNodes[i].childNodes.length === 1) {
                var value = element.childNodes[i].childNodes[0].nodeValue;
                var value_color = !isNaN(Number(value)) ? 'code-number' : 'code-string';
                var value_txt = '<span class="'+ value_color +'">' + value + '</span>';
                var item = t + '&nbsp;&nbsp;&nbsp;&nbsp;&lt;<span class="code-key">' + nodeName +
                    '</span>&gt;' + value_txt + '&lt;/<span class="code-key">' + nodeName + '</span>&gt;\n';

                list.push(item);
            } else {
                build_xml(++index, list, element.childNodes[i]);
            }
        }
        list.push(t + '&lt;/<span class="code-key">'+ element.nodeName +'</span>&gt;\n');
    }

    var list = [];
    build_xml(0, list, xml_doc.documentElement);

    document.getElementById(continner).innerHTML = list.join("");

    return list.join("");
}

function copy(obj){
    var newobj = {};
    for ( var attr in obj) {
        newobj[attr] = obj[attr];
    }
    return newobj;
}
