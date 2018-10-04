package client;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

// TODO: Credit to: https://stackoverflow.com/a/31761362 for the code
public class ScrabblePane extends Pane {
    private final ScrabbleCanvas canvas;
    private final TextField letterType;
    private int mark = 0;

    public Point getLetterType_cell() {
        return letterType_cell;
    }

    private Point letterType_cell;

    public ScrabblePane() {
        super();

        canvas = new ScrabbleCanvas(getWidth(), getHeight());
        letterType = new TextField();

        letterType.setVisible(false);
        letterType.setAlignment(Pos.CENTER);

        // only allow one letter
        letterType.setTextFormatter(
                new TextFormatter<String>((TextFormatter.Change change) -> {
            String newText = change.getControlNewText();
            System.out.println(change + "\t\t" + newText);

            if (newText.length() > 1)
                return null;
            else if (change.getControlText().length() == 0 &&
                    change.isAdded() && Character.isLetter(newText.charAt(0))) {
                change.setText(newText.toUpperCase());
            }
            else {
                change.setText("");
            }
            return change;
        }));

        letterType.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ENTER:
                        canvas.setLetter(
                                canvas.getSelectedCell().x,
                                canvas.getSelectedCell().y,
                                letterType.getText().isEmpty() ? null : letterType.getText().charAt(0));
                        letterType.setVisible(false);
                        canvas.requestFocus();
                        canvas.enabledProperty.set(!canvas.enabledProperty.get());
                        break;
                    case ESCAPE:
                        canvas.requestFocus();
                        letterType.setVisible(false);
                        break;
                    case UP:
                    case DOWN:
                    case LEFT:
                    case RIGHT:
                        canvas.setLetter(
                                canvas.getSelectedCell().x,
                                canvas.getSelectedCell().y,
                                letterType.getText().isEmpty() ? null : letterType.getText().charAt(0));
                        letterType.setVisible(false);
                        canvas.fireEvent(event);
                        canvas.enabledProperty.set(!canvas.enabledProperty.get());
                        break;
                    default:
                        if(!letterType.getText().isEmpty() && Character.isLetter(letterType.getText().charAt(0))) {
                            canvas.setLetter(
                                    canvas.getSelectedCell().x,
                                    canvas.getSelectedCell().y,
                                    letterType.getText().isEmpty() ? null : letterType.getText().charAt(0));
                            letterType.setVisible(false);
                            canvas.chosenCellProperty.set(canvas.getSelectedCell());
                            canvas.fireEvent(event);
                            canvas.enabledProperty.set(!canvas.enabledProperty.get());
                        }
                        break;
                }
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                openTextField();
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (letterType.isVisible()) {
                    canvas.setLetter(
                            letterType_cell.x,
                            letterType_cell.y,
                            letterType.getText().isEmpty() ? null : letterType.getText().charAt(0));
                    canvas.requestFocus();
                }

                letterType.setVisible(false);
            }
        });

        canvas.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case LEFT:
                    case RIGHT:
                    case UP:
                    case DOWN:
                        break;
                    default:
                        if (event.getText().length() == 1 &&
                                Character.isLetter(event.getText().charAt(0)) ||
                                event.getCode() == KeyCode.ENTER)
                            openTextField();
                        break;
                }

            }
        });

        canvas.widthProperty().addListener(o -> {
            // TODO: Why do I have to put this together and not in canvas?
            canvas.repaint();
            positionTextField();
            letterType.autosize();
        });
        canvas.heightProperty().addListener(o -> {
            canvas.repaint();
            positionTextField();
            letterType.autosize();
        } );

        canvas.enabledProperty.addListener(
                o -> {
                    if (!canvas.enabledProperty.get())
                        letterType.setVisible(false);
                });

        // make sure canvas scrabble board changes size whenever we stretch window
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());

        getChildren().add(canvas);
        getChildren().add(letterType);
    }

    public ScrabbleCanvas getCanvas() {
        return canvas;
    }

    private void positionTextField() {
        if (canvas.getSelectedCell() == null)
            return;

        System.out.println("NEW SIZE: " + getWidth() + " " + getHeight());
        Point2D selected_xy = canvas.toCell(canvas.getSelectedCell());
        Dimension2D cell_size = canvas.getCellSize();
        // move textbox to cell
        letterType.setLayoutX(selected_xy.getX());
        letterType.setLayoutY(selected_xy.getY());

        letterType.setPrefSize(cell_size.getWidth(), cell_size.getHeight());

        letterType.setPadding(new Insets(-0.05 * cell_size.getHeight(), 0,0, 0));

        Font f = canvas.getLetterFont();
        letterType.setFont(new Font(f.getName(), f.getSize()/1.2));
    }

    private void openTextField() {
        if (canvas.getSelectedCell() == null || !canvas.enabledProperty.get())
            return;

        positionTextField();

        letterType_cell = canvas.getSelectedCell();
        Character letter = canvas.getLetter(canvas.getSelectedCell().x, canvas.getSelectedCell().y);
        if (letter != null) {
            letterType.setText(letter.toString());
            letterType.setVisible(false);
        }
        else {
            letterType.setText("");
            letterType.setVisible(true);
            letterType.requestFocus();
        }
    }

    public int getMark() {
        int row = letterType_cell.x;
        int col = letterType_cell.y;
        boolean h = false;
        boolean v = false;
        int count = 0;
        for(int i = 1; getCanvas().letters.containsKey(new Point(row, col + i)) && col + i <= 19; i++) {
            h = true;
            count++;
        }
        for(int i = 1; getCanvas().letters.containsKey(new Point(row, col - i)) && col - i >= 0; i++) {
            h = true;
            count++;
        }
        for(int i = 1; getCanvas().letters.containsKey(new Point(row + i, col)) && row + i <= 19; i++) {
            v = true;
            count++;
        }
        for(int i = 1; getCanvas().letters.containsKey(new Point(row - i, col)) && row - i <= 0; i++) {
            v = true;
            count++;
        }
        if(h && v) {
            count++;
        }
        mark = mark + count + 1;
        return mark;
    }

    public class ScrabbleCanvas extends Canvas {
        private int num_rows = 20;
        private int num_cols = 20;

        private Map<Point, Character> letters = new HashMap<>();

        private Dimension2D cell_size;
        private final Color SELECTED_COLOR  = Color.LIGHTBLUE;
        private final Color NORMAL_COLOR = Color.WHITE;
        private final Color DISABLED_COLOR = Color.LIGHTGREY;
        private final Color PARTCHOSEN_COLOR = Color.YELLOW;
        private final Color CHOSEN_COLOR = Color.GREENYELLOW;
        private Color current_color = NORMAL_COLOR;
        private Point selected_cell;
        private double line_width = 1.2;

        // TODO: The cell that was typed in.
        public ObjectProperty<Point> chosenCellProperty;
        public BooleanProperty enabledProperty;

        public ScrabbleCanvas(double width, double height) {
            super(width, height);

            // BOOLEAN PROPERTIES

            enabledProperty = new SimpleBooleanProperty(true);
            enabledProperty.addListener(o -> {
                if (enabledProperty.get() == false)
                    selected_cell = null;

                current_color = enabledProperty.get() ? NORMAL_COLOR : DISABLED_COLOR;
                deepPaint();
            });

            chosenCellProperty = new SimpleObjectProperty<>(null);
            chosenCellProperty.addListener(o -> {
                deepPaint();
            });

            // END BOOLEAN PROPERTIES

            deepPaint();

            this.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
                if (enabledProperty.get()) {
                    shallowPaint(getCellHovering(e));
                    requestFocus();
                }

            });

            this.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
                Point new_selected_cell = null;
                System.out.println("I GOT PRESSED HERE");
                switch (e.getCode()) {
                    case LEFT:
                        if (selected_cell.getX() > 0)
                            new_selected_cell = new Point(selected_cell.x - 1, selected_cell.y);
                        break;
                    case RIGHT:
                        if (selected_cell.getX() < num_cols - 1)
                            new_selected_cell = new Point(selected_cell.x + 1, selected_cell.y);
                        break;
                    case DOWN:
                        if (selected_cell.getY() < num_rows - 1)
                            new_selected_cell = new Point(selected_cell.x, selected_cell.y + 1);
                        break;
                    case UP:
                        if (selected_cell.getY() > 0)
                            new_selected_cell = new Point(selected_cell.x, selected_cell.y - 1);
                        break;
                    default:
                        return;
                }

                if (new_selected_cell != null) {
                    shallowPaint(new_selected_cell);
                }

                requestFocus();
            });
        }

        public Dimension2D getCellSize() {
            return cell_size;
        }

        public Character getLetter(int row, int col) {
            return letters.get(new Point(row, col));
        }

        public void setLetter(int row, int col, Character c) {
            Point cell = new Point(row, col);

            if (c == null) {
                letters.remove(cell);
            } else {
                letters.put(cell, c);
            }

            refreshCell(getGraphicsContext2D(), cell);
        }

        public void removeLetter(int row, int col) {
            Point cell = new Point(row, col);
            letters.remove(cell);

            refreshCell(getGraphicsContext2D(), cell);
        }

        private Point getCellHovering(MouseEvent e) {
            int row =(int)(e.getX()/cell_size.getWidth());
            int col = (int)(e.getY()/cell_size.getHeight());

            return new Point(row, col);
        }

        public Point2D toCell(Point coordinate) {
            double x = coordinate.getX() * cell_size.getWidth();
            double y = coordinate.getY() * cell_size.getHeight();

            return new Point2D(x, y);
        }

        public Point getSelectedCell() {
            return selected_cell;
        }

        public Font getLetterFont() {
            double size = Math.min(cell_size.getHeight(), cell_size.getWidth());
            return new Font("Arial", (int)(size/1.1));
        }

        private Dimension2D measureText(Font f, String text) {
            javafx.scene.text.Text theText = new javafx.scene.text.Text(text);
            theText.setFont(f);

            return new Dimension2D(theText.getBoundsInLocal().getWidth(),
                    theText.getBoundsInLocal().getHeight());
        }

        private double snap(double val) {
            // TODO: Refer to https://stackoverflow.com/a/27847190 . This allows sharper lines by drawing it in 'middle of pixel'
            return (int)val + 0.5;
        }

        private void fillCell(GraphicsContext c, Point cell, Color color, boolean drawLetter) {
            if (cell == null)
                return;

            Point2D cell_xy = toCell(cell);

            // fill it clear first
            c.setFill(NORMAL_COLOR);
            c.fillRect(snap(cell_xy.getX()) + line_width,
                    snap(cell_xy.getY()) + line_width,
                    snap(cell_size.getWidth()) - line_width * 2,
                    snap(cell_size.getHeight()) - line_width * 2);

            c.setFill(color);
            c.fillRect(snap(cell_xy.getX()) + line_width * 1.2,
                    snap(cell_xy.getY()) + line_width * 1.2,
                    snap(cell_size.getWidth()) - line_width * 2.4,
                    snap(cell_size.getHeight()) - line_width * 2.4);


            if (drawLetter) {
                drawLetter(cell, c);
            }

            //drawBorders(c);
        }

        private void shallowPaint(Point new_selected_cell) {
            GraphicsContext c = getGraphicsContext2D();

            Point old_selected_cell = selected_cell;
            selected_cell = new_selected_cell;

            refreshCell(c, old_selected_cell);
            // TODO: ORDER IS IMPORTANT HERE. NEED TO FIX THIS.
            highlightChosenCell(c, true);
            refreshCell(c, selected_cell);
        }

        public void repaint() {
            deepPaint();
        }

        private void deepPaint() {
            GraphicsContext c = this.getGraphicsContext2D();
            // adjust cell sizes due to change in window size
            cell_size = new Dimension2D(getWidth() / num_cols, getHeight() / num_rows);
            c.setFont(getLetterFont());
            c.setFill(Color.BLACK);

            System.out.println("DEEP PAINTING");

            c.setFill(current_color);
            c.fillRect(0, 0, getWidth(), getHeight());

            // for the words horizontally/vertically part of the 'chosen cell'
            highlightChosenCell(c, false);

            // for the selected one
            refreshCell(c, selected_cell);

            // draw letters
            for (Point cell : letters.keySet()) {
                drawLetter(cell, c);
            }

            c.setLineWidth(line_width);
            c.setStroke(Color.BLACK);
            drawGrid(c);
        }

        private void highlightChosenCell(GraphicsContext c, boolean drawLetters) {
            if (chosenCellProperty.get() == null)
                return;

            fillCell(c, chosenCellProperty.get(), CHOSEN_COLOR, true);
            System.out.println("CHOSEN: " + chosenCellProperty.get());

            // TODO: Make this code better (prototyped for now)
            for (int dir : new int[] {1, -1}) {
                // horizontal
                for (int x = chosenCellProperty.get().x + dir; x < num_cols && x >= 0 ; x += dir) {
                    Point p = new Point(x, chosenCellProperty.get().y);
                    Character letter = letters.get(p);

                    if (letter != null)
                        fillCell(c, p, PARTCHOSEN_COLOR, drawLetters);
                    else
                        break;
                }

                // vertical
                for (int y = chosenCellProperty.get().y + dir; y < num_rows && y >= 0 ; y += dir) {
                    Point p = new Point(chosenCellProperty.get().x, y);
                    Character letter = letters.get(p);

                    if (letter != null)
                        fillCell(c, p, PARTCHOSEN_COLOR, drawLetters);
                    else
                        break;
                }
            }
        }

        private void refreshCell(GraphicsContext c, Point cell) {
            if (cell == null)
                return;

            Point2D cell_xy = toCell(cell);
            fillCell(c, cell, cell.equals(selected_cell) ? SELECTED_COLOR : current_color, true);
        }

        private void drawLetter(Point cell, GraphicsContext c) {
            if (letters.get(cell) == null)
                return;

            c.setFill(Color.BLACK);
            Point2D cell_xy = toCell(cell);
            Dimension2D char_size = measureText(c.getFont(), letters.get(cell).toString());

            double x = cell_xy.getX() + (cell_size.getWidth() - char_size.getWidth())/2.0;
            double y = cell_xy.getY() + cell_size.getHeight()/2.0 + char_size.getHeight()/3;

            //System.out.println("WRITE: " + cell + "\t\t" + new Point2D(x,y));

            c.fillText(letters.get(cell).toString(), x, y);
        }

        private void drawGrid(GraphicsContext c) {
            for (int x = 0; x < num_rows; x++) {
                c.strokeLine(snap(x * cell_size.getWidth()), 0,
                        snap(x * cell_size.getWidth()), getHeight());
            }

            for (int y = 0; y < num_cols; y++) {
                c.strokeLine(0, snap(y * cell_size.getHeight()),
                        getWidth(), snap(y * cell_size.getHeight()));
            }

            drawBorders(c);
        }

        private void drawBorders(GraphicsContext c) {
            // faces of the table need bolder lines
            c.setLineWidth(line_width * 2);
            c.strokeLine(0, 0,0, getHeight());
            c.strokeLine(getWidth(), 0, getWidth(), getHeight());
            c.strokeLine(0, 0, getWidth(), 0);
            c.strokeLine(0, getHeight(), getWidth(), getHeight());
        }
    }
}