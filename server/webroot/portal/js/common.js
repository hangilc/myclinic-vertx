function ajaxGet(url, data) {
    return new Promise((resolve, fail) => {
        $.ajax({
            type: "GET",
            url: url,
            data: data,
            cache: false,
            dataType: "json",
            success: resolve,
            error: (xhr, status, err) => fail(err)
        });
    });
}

function ajaxPost(url, data) {
    return new Promise((resolve, fail) => {
        $.ajax({
            type: "POST",
            url: url,
            data: JSON.stringify(data),
            cache: false,
            dataType: "json",
            success: resolve,
            error: (xhr, status, err) => fail(err)
        });
    });
}

function replaceElement(prevElement, newElement){
    prevElement.after(newElement);
    prevElement.detach();
}

