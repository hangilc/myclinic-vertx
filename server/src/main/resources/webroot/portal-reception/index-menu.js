
class Panel {
    constructor(name, ctor, link){
        this.name = name;
        this.ctor = ctor;
        this.link = link;
        this.savedElement = null;
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
        return this.paneWrapper.firstChild;
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
                await panel.ctor(panelWrap);
                this.panelWrapper.appendChild(panelWrap);
            } else {
                this.panelWrapper.appendChild(panel.savedElement);
            }
        }
    }

    setActive(name){
        for(let panel of this.panels){
            if( panel.name === name ){
                link.classList.add("active");
            } else {
                link.classList.remove("active");
            }
        }
    }
}
