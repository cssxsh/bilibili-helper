function imagesComplete() {
    const images = $('img');
    let complete = images.length !== 0;
    let count = 0;
    try {
        images.each((index, element) => {
            complete = complete && element.complete;
            element.complete && count++;
        });
    } finally {
        console.log(`ImagesComplete: ${count}/${images.length}`);
    }
    return complete;
}

const IsReady = () => document.readyState === 'complete' && imagesComplete()