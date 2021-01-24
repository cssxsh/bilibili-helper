
function findVue() {
    let Vue = null;
    try {
        for(const element of document.body.children) {
            Vue = Vue || element['__vue__'];
        }
    } finally {
        Vue = Vue || {};
    }
    return Vue;
}
function vmMounted(vm) {
    let mounted = vm['_isMounted'];
    try {
        if (Array.isArray(vm['$children'])) {
            for (const child of vm['$children']) {
                mounted = mounted && vmMounted(child);
            }
        }
    } finally {
        // console.log(`VmMounted: ${mounted}`);
    }
    return mounted;
}
function imagesComplete() {
    const images = document.getElementsByTagName("img");
    let complete = images.length !== 0;
    let count = 0;
    try {
        for(const element of images) {
            complete = complete && element.complete;
            element.complete && count++;
        }
    } finally {
        console.log(`ImagesComplete: ${count}/${images.length}`);
    }
    return complete;
}

const IsReady = () => document.readyState === 'complete' && vmMounted(findVue()) && imagesComplete();