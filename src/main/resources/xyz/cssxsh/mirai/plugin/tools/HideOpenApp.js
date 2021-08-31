function hide(name = '') {
    for (const btn of document.getElementsByClassName(name)) {
        btn.style.display = "none"
    }
}

const HideOpenApp = () => {
    hide('open-app')
    hide('launch-app-btn')
    hide('unlogin-popover')
    hide('no-login')
}