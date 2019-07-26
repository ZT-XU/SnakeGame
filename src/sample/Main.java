package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;

import java.util.*;


public class Main extends Application {
    // 变量
    private final static int MOVE = 40;
    // 游戏区域size
    private final static int WIDTH = 40; // 40 * 20, 作为边框
    private final static int HEIGH = 20;
    // 菜单区域size;
    private final static int HEIGHMENU = 2;

    // 类
    private AnchorPane pane = new AnchorPane();
    private Pane background = new Pane();
    private Scene scene = new Scene(background, WIDTH * MOVE + 20, (HEIGHMENU + HEIGH) * MOVE + 10);

    // snake
    private List<Position> snake = new ArrayList<>();
    private int speed = 6; // 20最大
    // direction
    private Dir direction = Dir.right;
    // point
    private int points = -1;
    // head
    private ImagePattern headImage = new ImagePattern(new Image(new File("D:\\IdeaProjects\\SnakeGame\\src\\sample\\head2.png")
            .toURI().toString()));
    private ImagePattern headImageRL = new ImagePattern(
            new Image(new File("D:\\IdeaProjects\\SnakeGame\\src\\sample\\headRL.png").toURI().toString()));

    // food
    private int foodX = 0;
    private int foodY = 0;
    private Random random = new Random(); // 随机产生食物
    private Color color;
    // other
    private static boolean isGameOver = false;
    private static boolean pause = false;

    // 文本
    private Text stop = new Text("");
    private Text gameOver = new Text("");
    private Text score = new Text("得分： ");


    @Override
    public void start(Stage primaryStage) {
        Rectangle rectangle = new Rectangle(WIDTH * MOVE, 2 * MOVE - 10);
        rectangle.setFill(Color.BLUEVIOLET);
        rectangle.setX(10);
        rectangle.setY(5);
        rectangle.setFill(new ImagePattern(new Image(new File("D:\\IdeaProjects\\SnakeGame\\src\\sample\\images.jpg")
                .toURI().toString())));

        pane.setLayoutX(10);
        pane.setLayoutY(2 * MOVE);
        pane.setPrefSize(WIDTH * MOVE, HEIGH * MOVE);
        pane.setStyle("-fx-background-color: black");
        background.getChildren().add(pane);
        scene.setFill(Color.LIGHTGRAY);

        // 初始化文字
        stop.setFont(new Font("华文楷体", 60));
        stop.setX(WIDTH * MOVE / 2 - 40);
        stop.setY(HEIGH * MOVE / 2 - 40);
        stop.setFill(Color.LEMONCHIFFON);

        gameOver.setFont(new Font("华文楷体", 120));
        gameOver.setX(WIDTH * MOVE / 2 - 200);
        gameOver.setY(HEIGH * MOVE / 2 - 40);
        gameOver.setFill(Color.RED);

        score.setFont(new Font("华文楷体", 25));
        score.setX(MOVE);
        score.setY(30);

        background.getChildren().addAll(rectangle, score);

        Canvas canvas = new Canvas(WIDTH * 40, HEIGH * 40);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        pane.getChildren().addAll(canvas, stop, gameOver);
        // 时钟任务
        AnimationTimer timer = new AnimationTimer() {
            long lastTick = 0;

            @Override
            public void handle(long now) {

                if (lastTick == 0) {
                    lastTick = now;
                    flash(graphicsContext);
                    return;
                }

                if (now - lastTick > 1000000000 / (speed / 2)) {
                    if (isGameOver) {
                        gameOver.setText("游戏结束");
                        return;
                    }
                    lastTick = now;
                    flash(graphicsContext);
                }

            }
        };
        timer.start();
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP && direction != Dir.down)
                direction = Dir.up;
            else if (event.getCode() == KeyCode.DOWN && direction != Dir.up)
                direction = Dir.down;
            else if (event.getCode() == KeyCode.LEFT && direction != Dir.right)
                direction = Dir.left;
            else if (event.getCode() == KeyCode.RIGHT && direction != Dir.left)
                direction = Dir.right;
            else if (event.getCode() == KeyCode.SPACE) {
                pause = !pause;
                if (pause) {
                    timer.stop();
                    stop.setText("暂停");
                } else {
                    timer.start();
                    stop.setText("");
                }
            }
        });


        // 设置初始位置
        snake.add(new Position(MOVE * 4, MOVE));
        snake.add(new Position(MOVE * 3, MOVE));
        snake.add(new Position(MOVE * 2, MOVE));
        // food初始
        newFood();

        primaryStage.setScene(scene);
        primaryStage.setTitle("贪吃蛇");
        primaryStage.show();

        // 图标设置
        primaryStage.getIcons().add(
                new Image(new File("D:\\IdeaProjects\\SnakeGame\\src\\sample\\icon.png").toURI().toString()));

        // 窗口关闭方法
        primaryStage.setOnCloseRequest(event -> System.exit(0));
    }

    public static void main(String[] args) {
        launch(args);
    }

    // 内部类Position
    class Position {
        int x, y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    enum Dir {
        left, right, up, down
    }

    // 刷新
    private void flash(GraphicsContext graphicsContext) {

        for (int i = snake.size() - 1; i >= 1; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }


        switch (direction) {
            case right:
                snake.get(0).x += MOVE;
                if (snake.get(0).x >= WIDTH * MOVE)
                    isGameOver = true;
                break;
            case up:
                snake.get(0).y -= MOVE;
                if (snake.get(0).y < 0)
                    isGameOver = true;
                break;
            case down:
                snake.get(0).y += MOVE;
                if (snake.get(0).y >= HEIGH * MOVE)
                    isGameOver = true;
                break;
            case left:
                snake.get(0).x -= MOVE;
                if (snake.get(0).x < 0)
                    isGameOver = true;
                break;
        }

        // 判断自咬
        for (Position p : snake) {
            if (snake.get(0) != p && snake.get(0).x == p.x && snake.get(0).y == p.y)
                isGameOver = true;
        }

        // 移动之后判断吃食物，吃了正好消除
        if (foodX == snake.get(0).x && foodY == snake.get(0).y) {

            graphicsContext.setStroke(color);
            // 先隐藏起来,反正后面会更新
            snake.add(new Position(-100, -100));
            newFood();
        }

        // 清除背景
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, WIDTH * MOVE, HEIGH * MOVE);

        graphicsContext.setFill(color);
        graphicsContext.fillOval(foodX, foodY, MOVE, MOVE);
        if (points < 1)
            graphicsContext.setStroke(Color.CORNFLOWERBLUE);
        if (direction == Dir.right || direction == Dir.left)
            graphicsContext.setFill(headImageRL);
        else
            graphicsContext.setFill(headImage);
        graphicsContext.setLineWidth(2);
        for (int i = 0; i < snake.size(); i++) {

            if (i == 0) {
                graphicsContext.fillOval(snake.get(i).x, snake.get(i).y, MOVE, MOVE);
            } else
                graphicsContext.strokeOval(snake.get(i).x, snake.get(i).y, MOVE - 2, MOVE - 2);
        }
    }

    private void newFood() {
        points++;
        loop:
        while (true) {
            foodX = random.nextInt((WIDTH - 2) * MOVE) / MOVE + 1;
            foodY = random.nextInt((HEIGH - 2) * MOVE) / MOVE + 1;
            foodX *= MOVE;
            foodY *= MOVE;
            for (Position p : snake) {
                if (p.x == foodX && p.y == foodY) {
                    continue loop;
                }
            }
            break;
        }
        int col = (int) (random.nextDouble() * 100);
        color = Color.PALEVIOLETRED;
        if (col < 10)
            color = Color.CORNFLOWERBLUE;
        else if (col < 20)
            color = Color.SPRINGGREEN;
        else if (col < 30)
            color = Color.HOTPINK;
        else if (col < 40)
            color = Color.ORANGE;
        else if (col < 50)
            color = Color.YELLOW;
        else if (col < 60)
            color = Color.ALICEBLUE;
        else if (col < 70)
            color = Color.GHOSTWHITE;
        else if (col < 80)
            color = Color.LIGHTGOLDENRODYELLOW;
        else if (col < 90)
            color = Color.PALEVIOLETRED;
        else
            color = Color.FORESTGREEN;

        // 加速，最大到18;
        if (speed < 18)
            speed++;

        score.setText("得分：" + points);
    }

}

