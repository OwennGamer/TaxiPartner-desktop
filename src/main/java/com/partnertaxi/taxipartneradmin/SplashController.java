package com.partnertaxi.taxipartneradmin;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class SplashController {

    @FXML
    private StackPane splashRoot;

    @FXML
    private ImageView logoImage;

    @FXML
    private Label welcomeTitle;

    @FXML
    private Label welcomeSubtitle;

    @FXML
    private VBox contentBox;

    @FXML
    public void initialize() {
        splashRoot.setOpacity(0);
        contentBox.setOpacity(0);
        logoImage.setScaleX(0.85);
        logoImage.setScaleY(0.85);
        logoImage.setTranslateY(14);

        FadeTransition rootFade = new FadeTransition(Duration.millis(650), splashRoot);
        rootFade.setFromValue(0);
        rootFade.setToValue(1);

        FadeTransition contentFade = new FadeTransition(Duration.millis(900), contentBox);
        contentFade.setFromValue(0);
        contentFade.setToValue(1);
        contentFade.setDelay(Duration.millis(180));

        ScaleTransition logoScale = new ScaleTransition(Duration.millis(950), logoImage);
        logoScale.setFromX(0.85);
        logoScale.setFromY(0.85);
        logoScale.setToX(1.0);
        logoScale.setToY(1.0);
        logoScale.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition logoRise = new TranslateTransition(Duration.millis(950), logoImage);
        logoRise.setFromY(14);
        logoRise.setToY(0);
        logoRise.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition titleFade = new FadeTransition(Duration.millis(820), welcomeTitle);
        titleFade.setFromValue(0);
        titleFade.setToValue(1);
        titleFade.setDelay(Duration.millis(350));

        FadeTransition subtitleFade = new FadeTransition(Duration.millis(800), welcomeSubtitle);
        subtitleFade.setFromValue(0);
        subtitleFade.setToValue(1);
        subtitleFade.setDelay(Duration.millis(560));

        new ParallelTransition(rootFade, contentFade, logoScale, logoRise, titleFade, subtitleFade).play();
    }
}
