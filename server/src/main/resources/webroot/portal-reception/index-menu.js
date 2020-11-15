
class Panel {
    constructor(name, ctor, link){
        this.name = name;
        this.ctor = ctor;
        this.link = link;
        this.reloadHook = async () => {};
        this.savedElement = null;
    }

    setReloadHook(hook){
        this.reloadHook = hook;
    }
}

export class Menu {
    constructor(panelWrapper) {
        this.panelWrapper = panelWrapper;
        this.panels = [];
        this.currentPanel = null;
    }

    addItem(name, ctor, link) {
        this.panels.push(new Panel(name, ctor, link));
        link.addEventListener("click", async event => await this.simulateClick(name));
    }

    findPanelByName(name){
        for(let panel of this.panels ){
            if( panel.name === name ){
                return panel;
            }
        }
    }

    getCurrentPanelContent(){
        return this.panelWrapper.firstChild;
    }

    async simulateClick(name){
        if( !this.currentPanel || this.currentPanel.name !== name ){
            let panel = this.findPanelByName(name);
            if( !panel ){
                console.log("No such panel: " + name);
                return;
            }
            if( this.currentPanel ){
                this.currentPanel.savedElement = this.getCurrentPanelContent();
            }
            this.panelWrapper.innerHTML = "";
            if( !panel.savedElement ){
                let panelWrap = document.createElement("div");
                panelWrap.classList.add("panel");
                let config = await panel.ctor(panelWrap);
                if( config.reloadHook ){
                    panel.setReloadHook(config.reloadHook);
                    await panel.reloadHook();
                }
                this.panelWrapper.appendChild(panelWrap);
            } else {
                await panel.reloadHook();
                this.panelWrapper.appendChild(panel.savedElement);
            }
            this.currentPanel = panel;
            this.setActive(name);
        }
    }

    setActive(name){
        for(let panel of this.panels){
            if( panel.name === name ){
                panel.link.classList.add("active");
            } else {
                panel.link.classList.remove("active");
            }
        }
    }
}
