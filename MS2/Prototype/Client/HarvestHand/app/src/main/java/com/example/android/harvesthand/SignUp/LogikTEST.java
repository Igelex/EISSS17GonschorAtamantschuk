
public void requestTutorialFromServer(){}

        /*Setze Backgroundfarbe für alle Icons*/
        for(eigenschaft in tutorial){
            switch(deviation){
            case eigenschaft.deviation<=10%:
            icon.setImageResource(Grün);
            break;
            case eigenschaft.deviation<=20%:
            icon.setImageResource(Gelb);
            break;
            case eigenschaft.deviation<=30%:
            icon.setImageResource(Orange);
            break;
            case eigenschaft.deviation>30%:
            icon.setImageResource(Rot);
            break;
            }

            if(eigenschaft.status = NORM){
            /*Zeige eine standarisierte Anbauempfehlung zur dieser Eigenschaft*/
            bilder = readFiles(bilder.norm);
            /*Erstelle empfehlung aus Bildern, Animationen etc.*/
            empfehlung.setImageResource(bilder);
        } else if (eigenschaft.status = LESS)
            /*Zeige eine  Anbauempfehlung zur verbesserung der Eigenschaft*/
            bilder = readFiles(bilder.less);
            /*Erstelle empfehlung aus Bildern, Animationen etc.*/
            empfehlung.setImageResource(bilder);
        } else (eigenschaft.status = GRAETER){
            /*Zeige eine  Anbauempfehlung zur verbesserung der Eigenschaft*/
            bilder = readFiles(bilder.graeter);
            /*Erstelle empfehlung aus Bildern, Animationen etc.*/
             empfehlung.setImageResource(bilder);
        }

        /*Vertone Textinhalte*/
        uiElement.setOnKlickListener(){
            speaker.speak(uiElement.getText());
        }



