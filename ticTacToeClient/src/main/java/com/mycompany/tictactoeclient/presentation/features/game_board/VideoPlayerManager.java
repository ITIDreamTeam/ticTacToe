/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.presentation.features.game_board;

import java.net.URL;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 *
 * @author yasse
 */
public class VideoPlayerManager {
    
    private static VideoPlayerManager instance;
    private MediaPlayer currentPlayer;
    private Stage currentStage;
    private boolean isPlaying = false;
    private int retryCount = 0;
    private static final int MAX_RETRIES = 3;
    
    private VideoPlayerManager() {}
    
    public static VideoPlayerManager getInstance() {
        if (instance == null) {
            instance = new VideoPlayerManager();
        }
        return instance;
    }
    
    public void playVideo(URL videoResource, Stage ownerStage, Runnable onComplete) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> playVideo(videoResource, ownerStage, onComplete));
            return;
        }
        
        if (videoResource == null) {
            System.err.println("Video resource is null");
            executeCallback(onComplete);
            return;
        }
        
        if (ownerStage == null || !ownerStage.isShowing()) {
            System.err.println("Owner stage is null or not showing");
            executeCallback(onComplete);
            return;
        }
        
        if (isPlaying) {
            System.out.println("Video already playing, waiting...");
            PauseTransition wait = new PauseTransition(Duration.millis(500));
            wait.setOnFinished(e -> playVideo(videoResource, ownerStage, onComplete));
            wait.play();
            return;
        }
        
        isPlaying = true;
        retryCount = 0;
        
        createAndShowVideo(videoResource, ownerStage, onComplete);
    }
    
    private void createAndShowVideo(URL videoResource, Stage ownerStage, Runnable onComplete) {
        try {
            System.out.println("Creating video from: " + videoResource);
            
            Media media = new Media(videoResource.toExternalForm());
            
            MediaPlayer player = new MediaPlayer(media);
            currentPlayer = player;
            
            MediaView mediaView = new MediaView(player);
            mediaView.setFitWidth(400);
            mediaView.setFitHeight(250);
            mediaView.setPreserveRatio(true);
            
            StackPane root = new StackPane(mediaView);
            root.setStyle("-fx-background-color: black; -fx-border-color: #3fefef; -fx-border-width: 2;");
            
            Stage videoStage = new Stage(StageStyle.UNDECORATED);
            videoStage.initModality(Modality.APPLICATION_MODAL);
            videoStage.initOwner(ownerStage);
            videoStage.setScene(new Scene(root, 400, 250));
            currentStage = videoStage;
            
            videoStage.setX(ownerStage.getX() + (ownerStage.getWidth() / 2) - 200);
            videoStage.setY(ownerStage.getY() + (ownerStage.getHeight() / 2) - 125);
            
            Runnable cleanup = () -> {
                System.out.println("Cleaning up video resources");
                try {
                    if (currentPlayer != null) {
                        currentPlayer.stop();
                        currentPlayer.dispose();
                        currentPlayer = null;
                    }
                } catch (Exception e) {
                    System.err.println("Error disposing player: " + e.getMessage());
                }
                
                try {
                    if (currentStage != null && currentStage.isShowing()) {
                        currentStage.close();
                        currentStage = null;
                    }
                } catch (Exception e) {
                    System.err.println("Error closing stage: " + e.getMessage());
                }
                
                isPlaying = false;
                executeCallback(onComplete);
            };
            
            player.setOnError(() -> {
                System.err.println("MediaPlayer error: " + player.getError());
                handleVideoError(videoResource, ownerStage, onComplete, cleanup);
            });
            
            player.setOnEndOfMedia(() -> {
                System.out.println("Video ended normally");
                cleanup.run();
            });
            
            player.setOnReady(() -> {
                System.out.println("MediaPlayer ready, starting playback");
                try {
                    videoStage.show();
                    
                    PauseTransition playDelay = new PauseTransition(Duration.millis(200));
                    playDelay.setOnFinished(e -> {
                        try {
                            player.play();
                            System.out.println("Video playing");
                        } catch (Exception ex) {
                            System.err.println("Error starting playback: " + ex.getMessage());
                            cleanup.run();
                        }
                    });
                    playDelay.play();
                } catch (Exception e) {
                    System.err.println("Error in onReady: " + e.getMessage());
                    cleanup.run();
                }
            });
            
            PauseTransition watchdog = new PauseTransition(Duration.seconds(15));
            watchdog.setOnFinished(e -> {
                if (isPlaying) {
                    System.out.println("Video watchdog timeout - forcing cleanup");
                    cleanup.run();
                }
            });
            watchdog.play();
            
        } catch (Exception e) {
            System.err.println("Exception creating video: " + e.getMessage());
            e.printStackTrace();
            handleVideoError(videoResource, ownerStage, onComplete, null);
        }
    }
    
    private void handleVideoError(URL videoResource, Stage ownerStage, Runnable onComplete, Runnable cleanup) {
        retryCount++;
        
        if (retryCount < MAX_RETRIES) {
            System.out.println("Video error, retry " + retryCount + "/" + MAX_RETRIES);
            
            try {
                if (currentPlayer != null) {
                    currentPlayer.dispose();
                    currentPlayer = null;
                }
                if (currentStage != null && currentStage.isShowing()) {
                    currentStage.close();
                    currentStage = null;
                }
            } catch (Exception e) {
            }
            
            isPlaying = false;
            
            // Retry after delay
            PauseTransition retry = new PauseTransition(Duration.millis(500));
            retry.setOnFinished(e -> playVideo(videoResource, ownerStage, onComplete));
            retry.play();
        } else {
            System.err.println("Video failed after " + MAX_RETRIES + " attempts, skipping");
            
            if (cleanup != null) {
                cleanup.run();
            } else {
                isPlaying = false;
                executeCallback(onComplete);
            }
        }
    }
    
    private void executeCallback(Runnable callback) {
        if (callback != null) {
            // Small delay to ensure cleanup is complete
            PauseTransition delay = new PauseTransition(Duration.millis(100));
            delay.setOnFinished(e -> {
                Platform.runLater(() -> {
                    System.out.println("Executing video completion callback");
                    callback.run();
                });
            });
            delay.play();
        }
    }
    
    public void forceStop() {
        if (isPlaying) {
            System.out.println("Force stopping video");
            try {
                if (currentPlayer != null) {
                    currentPlayer.stop();
                    currentPlayer.dispose();
                    currentPlayer = null;
                }
                if (currentStage != null && currentStage.isShowing()) {
                    currentStage.close();
                    currentStage = null;
                }
            } catch (Exception e) {
                System.err.println("Error in force stop: " + e.getMessage());
            }
            isPlaying = false;
        }
    }
    public boolean isVideoPlaying() {
        return isPlaying;
    }
}
