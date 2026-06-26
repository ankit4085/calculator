import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

/**
 * Scientific Calculator — Java Swing
 *
 * Compile:  javac ScientificCalculator.java
 * Run:      java ScientificCalculator
 *
 * Features:
 *   Basic:      + − × ÷  %  ±  CE  C  ⌫
 *   Scientific: sin cos tan | asin acos atan (INV)
 *               log ln | x² x³ xʸ √ ³√
 *               π  e  1/x  n!  EXP (×10ⁿ)
 *   Modes:      DEG / RAD toggle
 *   Memory:     MS  MR  M+  M−  MC
 */
public class ScientificCalculator extends JFrame {

    // ── Display ──────────────────────────────────────────────────
    private JTextField expressionField;   // shows running expression
    private JTextField displayField;      // main numeric display

    // ── State ─────────────────────────────────────────────────────
    private double currentValue  = 0;
    private double memory        = 0;
    private double storedValue   = 0;
    private String operator      = "";
    private boolean newNumber    = true;
    private boolean isDegree     = true;   // DEG mode
    private boolean isInverse    = false;  // INV mode
    private String  expression   = "";

    private final DecimalFormat df = new DecimalFormat("#.##########");

    // ── Colours ───────────────────────────────────────────────────
    private static final Color BG          = new Color(28,  28,  35);
    private static final Color DISPLAY_BG  = new Color(18,  18,  24);
    private static final Color BTN_NUM     = new Color(55,  55,  68);
    private static final Color BTN_OP      = new Color(255, 147,  41);   // orange
    private static final Color BTN_SCI     = new Color(40,  90, 160);    // blue
    private static final Color BTN_MEM     = new Color(80,  60, 120);    // purple
    private static final Color BTN_FUNC    = new Color(50,  55,  70);    // dark-gray
    private static final Color BTN_SPECIAL = new Color(200,  60,  60);   // red
    private static final Color TEXT_LIGHT  = new Color(240, 240, 245);
    private static final Color TEXT_DIM    = new Color(160, 165, 185);
    private static final Color HOVER_TINT  = new Color(255, 255, 255, 25);

    // ── Entry point ───────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new ScientificCalculator().setVisible(true);
        });
    }

    // ── Constructor ───────────────────────────────────────────────
    public ScientificCalculator() {
        setTitle("Scientific Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        add(buildDisplay(), BorderLayout.NORTH);
        add(buildButtons(), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        updateDisplay("0");
    }

    // ── Display panel ─────────────────────────────────────────────
    private JPanel buildDisplay() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(DISPLAY_BG);
        panel.setBorder(new EmptyBorder(14, 16, 10, 16));

        expressionField = new JTextField("");
        expressionField.setEditable(false);
        expressionField.setBackground(DISPLAY_BG);
        expressionField.setForeground(TEXT_DIM);
        expressionField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        expressionField.setBorder(BorderFactory.createEmptyBorder());
        expressionField.setHorizontalAlignment(SwingConstants.RIGHT);

        displayField = new JTextField("0");
        displayField.setEditable(false);
        displayField.setBackground(DISPLAY_BG);
        displayField.setForeground(TEXT_LIGHT);
        displayField.setFont(new Font("SansSerif", Font.BOLD, 36));
        displayField.setBorder(BorderFactory.createEmptyBorder());
        displayField.setHorizontalAlignment(SwingConstants.RIGHT);

        panel.add(expressionField, BorderLayout.NORTH);
        panel.add(displayField,    BorderLayout.CENTER);
        return panel;
    }

    // ── Button grid ───────────────────────────────────────────────
    private JPanel buildButtons() {
        JPanel grid = new JPanel(new GridLayout(0, 5, 5, 5));
        grid.setBackground(BG);
        grid.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Row 1 — mode toggles & memory
        grid.add(btn("DEG",  BTN_FUNC));
        grid.add(btn("INV",  BTN_FUNC));
        grid.add(btn("MC",   BTN_MEM));
        grid.add(btn("MR",   BTN_MEM));
        grid.add(btn("MS",   BTN_MEM));

        // Row 2 — memory ops & clear
        grid.add(btn("M+",   BTN_MEM));
        grid.add(btn("M−",   BTN_MEM));
        grid.add(btn("CE",   BTN_SPECIAL));
        grid.add(btn("C",    BTN_SPECIAL));
        grid.add(btn("⌫",   BTN_SPECIAL));

        // Row 3 — trig row 1
        grid.add(btn("sin",  BTN_SCI));
        grid.add(btn("cos",  BTN_SCI));
        grid.add(btn("tan",  BTN_SCI));
        grid.add(btn("π",    BTN_SCI));
        grid.add(btn("e",    BTN_SCI));

        // Row 4 — trig row 2 / powers
        grid.add(btn("x²",   BTN_SCI));
        grid.add(btn("x³",   BTN_SCI));
        grid.add(btn("xʸ",   BTN_SCI));
        grid.add(btn("√",    BTN_SCI));
        grid.add(btn("³√",   BTN_SCI));

        // Row 5 — log & misc
        grid.add(btn("log",  BTN_SCI));
        grid.add(btn("ln",   BTN_SCI));
        grid.add(btn("1/x",  BTN_SCI));
        grid.add(btn("n!",   BTN_SCI));
        grid.add(btn("EXP",  BTN_SCI));

        // Row 6 — 7 8 9 ÷ %
        grid.add(btn("7",    BTN_NUM));
        grid.add(btn("8",    BTN_NUM));
        grid.add(btn("9",    BTN_NUM));
        grid.add(btn("÷",    BTN_OP));
        grid.add(btn("%",    BTN_FUNC));

        // Row 7 — 4 5 6 × ±
        grid.add(btn("4",    BTN_NUM));
        grid.add(btn("5",    BTN_NUM));
        grid.add(btn("6",    BTN_NUM));
        grid.add(btn("×",    BTN_OP));
        grid.add(btn("±",    BTN_FUNC));

        // Row 8 — 1 2 3 −
        grid.add(btn("1",    BTN_NUM));
        grid.add(btn("2",    BTN_NUM));
        grid.add(btn("3",    BTN_NUM));
        grid.add(btn("−",    BTN_OP));

        JButton eq = btn("=", BTN_OP);
        eq.setPreferredSize(new Dimension(0, 0));
        // "=" spans rows 8-9 in column 5 — achieved via helper panel
        JPanel eqWrap = new JPanel(new BorderLayout());
        eqWrap.setBackground(BG);
        eqWrap.add(eq);
        // placeholder to keep grid aligned
        grid.add(eqWrap);

        // Row 9 — 0 . + =
        JButton zeroBtn = btn("0", BTN_NUM);
        grid.add(zeroBtn);
        grid.add(btn(".",    BTN_NUM));
        grid.add(btn("+",    BTN_OP));
        grid.add(btn("=",    BTN_OP));
        grid.add(new JLabel()); // filler

        return grid;
    }

    // ── Button factory ────────────────────────────────────────────
    private JButton btn(String label, Color bg) {
        JButton b = new JButton(label);
        b.setBackground(bg);
        b.setForeground(TEXT_LIGHT);
        b.setFont(new Font("SansSerif", Font.BOLD, 15));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setBorder(new EmptyBorder(10, 6, 10, 6));

        // Hover effect
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(bg.brighter()); }
            public void mouseExited (MouseEvent e) { b.setBackground(bg); }
        });

        b.addActionListener(e -> handleButton(label));
        return b;
    }

    // ── Event dispatcher ──────────────────────────────────────────
    private void handleButton(String cmd) {
        switch (cmd) {
            // ── Digits ──
            case "0": case "1": case "2": case "3": case "4":
            case "5": case "6": case "7": case "8": case "9":
                inputDigit(cmd); break;

            case ".": inputDot();   break;
            case "±": negate();     break;
            case "%": percent();    break;

            // ── Basic operators ──
            case "+": case "−": case "×": case "÷":
                setOperator(cmd); break;

            case "=": calculate(); break;
            case "EXP": inputExponent(); break;

            // ── Clear ──
            case "C":  clearAll(); break;
            case "CE": clearEntry(); break;
            case "⌫":  backspace(); break;

            // ── Scientific ──
            case "sin":  applyTrig("sin"); break;
            case "cos":  applyTrig("cos"); break;
            case "tan":  applyTrig("tan"); break;
            case "log":  applyLog("log"); break;
            case "ln":   applyLog("ln");  break;
            case "x²":   applyPower(2);   break;
            case "x³":   applyPower(3);   break;
            case "xʸ":   setOperator("xʸ"); break;
            case "√":    applySqrt(2);    break;
            case "³√":   applySqrt(3);    break;
            case "1/x":  reciprocal();    break;
            case "n!":   factorial();     break;
            case "π":    insertConstant(Math.PI, "π"); break;
            case "e":    insertConstant(Math.E,  "e"); break;

            // ── Mode toggles ──
            case "DEG":  toggleDegRad();  break;
            case "INV":  toggleInverse(); break;

            // ── Memory ──
            case "MC":  memory = 0; showBrief("M cleared"); break;
            case "MR":  recallMemory();  break;
            case "MS":  memory = currentValue; showBrief("M = " + fmt(memory)); break;
            case "M+":  memory += currentValue; showBrief("M = " + fmt(memory)); break;
            case "M−":  memory -= currentValue; showBrief("M = " + fmt(memory)); break;
        }
    }

    // ── Input helpers ─────────────────────────────────────────────
    private void inputDigit(String d) {
        String cur = displayField.getText();
        if (newNumber) {
            cur = d;
            newNumber = false;
        } else {
            cur = cur.equals("0") ? d : cur + d;
        }
        displayField.setText(cur);
        currentValue = Double.parseDouble(cur);
    }

    private void inputDot() {
        if (newNumber) { displayField.setText("0."); newNumber = false; return; }
        if (!displayField.getText().contains("."))
            displayField.setText(displayField.getText() + ".");
    }

    private void inputExponent() {
        String cur = displayField.getText();
        if (!cur.contains("E") && !cur.contains("e"))
            displayField.setText(cur + "E");
        newNumber = false;
    }

    private void negate() {
        currentValue = -currentValue;
        updateDisplay(fmt(currentValue));
    }

    private void percent() {
        currentValue /= 100;
        updateDisplay(fmt(currentValue));
        newNumber = true;
    }

    // ── Operators ─────────────────────────────────────────────────
    private void setOperator(String op) {
        if (!operator.isEmpty() && !newNumber) calculate();
        storedValue = currentValue;
        operator    = op;
        newNumber   = true;
        expression  = fmt(storedValue) + " " + op;
        expressionField.setText(expression);
    }

    private void calculate() {
        if (operator.isEmpty()) return;
        double result;
        try {
            result = applyOperator(storedValue, currentValue, operator);
        } catch (ArithmeticException ex) {
            showError(ex.getMessage()); return;
        }
        expression += " " + fmt(currentValue) + " =";
        expressionField.setText(expression);
        currentValue = result;
        updateDisplay(fmt(result));
        operator  = "";
        newNumber = true;
        expression = "";
    }

    private double applyOperator(double a, double b, String op) {
        return switch (op) {
            case "+"  -> a + b;
            case "−"  -> a - b;
            case "×"  -> a * b;
            case "÷"  -> {
                if (b == 0) throw new ArithmeticException("Div by zero");
                yield a / b;
            }
            case "xʸ" -> Math.pow(a, b);
            default   -> b;
        };
    }

    // ── Clear & Backspace ─────────────────────────────────────────
    private void clearAll() {
        currentValue = storedValue = 0;
        operator = expression = "";
        newNumber = true;
        expressionField.setText("");
        updateDisplay("0");
    }

    private void clearEntry() {
        currentValue = 0;
        newNumber = true;
        updateDisplay("0");
    }

    private void backspace() {
        String s = displayField.getText();
        if (s.length() > 1) s = s.substring(0, s.length() - 1);
        else s = "0";
        displayField.setText(s);
        try { currentValue = Double.parseDouble(s); }
        catch (NumberFormatException ignored) { currentValue = 0; }
    }

    // ── Scientific operations ─────────────────────────────────────
    private void applyTrig(String fn) {
        double angle = isDegree ? Math.toRadians(currentValue) : currentValue;
        double result = switch (fn) {
            case "sin" -> isInverse ? (isDegree ? Math.toDegrees(Math.asin(currentValue)) : Math.asin(currentValue)) : Math.sin(angle);
            case "cos" -> isInverse ? (isDegree ? Math.toDegrees(Math.acos(currentValue)) : Math.acos(currentValue)) : Math.cos(angle);
            case "tan" -> isInverse ? (isDegree ? Math.toDegrees(Math.atan(currentValue)) : Math.atan(currentValue)) : Math.tan(angle);
            default    -> currentValue;
        };
        String label = (isInverse ? "a" : "") + fn + (isDegree ? "°" : "ʳ");
        expressionField.setText(label + "(" + fmt(currentValue) + ") =");
        currentValue = result;
        updateDisplay(fmt(result));
        newNumber = true;
    }

    private void applyLog(String fn) {
        if (currentValue <= 0) { showError("Domain error"); return; }
        double result = fn.equals("log") ?
                (isInverse ? Math.pow(10, currentValue) : Math.log10(currentValue)) :
                (isInverse ? Math.exp(currentValue)     : Math.log(currentValue));
        String label = isInverse ? (fn.equals("log") ? "10^x" : "eˣ") : fn;
        expressionField.setText(label + "(" + fmt(currentValue) + ") =");
        currentValue = result;
        updateDisplay(fmt(result));
        newNumber = true;
    }

    private void applyPower(int exp) {
        double result = Math.pow(currentValue, exp);
        expressionField.setText(fmt(currentValue) + (exp == 2 ? "²" : "³") + " =");
        currentValue = result;
        updateDisplay(fmt(result));
        newNumber = true;
    }

    private void applySqrt(int root) {
        if (currentValue < 0) { showError("Domain error"); return; }
        double result = Math.pow(currentValue, 1.0 / root);
        expressionField.setText((root == 2 ? "√" : "³√") + "(" + fmt(currentValue) + ") =");
        currentValue = result;
        updateDisplay(fmt(result));
        newNumber = true;
    }

    private void reciprocal() {
        if (currentValue == 0) { showError("Div by zero"); return; }
        expressionField.setText("1 ÷ " + fmt(currentValue) + " =");
        currentValue = 1.0 / currentValue;
        updateDisplay(fmt(currentValue));
        newNumber = true;
    }

    private void factorial() {
        long n = (long) currentValue;
        if (n < 0 || currentValue != n) { showError("Domain error"); return; }
        if (n > 20)                      { showError("Too large");    return; }
        long r = 1;
        for (long i = 2; i <= n; i++) r *= i;
        expressionField.setText(n + "! =");
        currentValue = r;
        updateDisplay(fmt(currentValue));
        newNumber = true;
    }

    private void insertConstant(double val, String name) {
        expressionField.setText(name);
        currentValue = val;
        updateDisplay(fmt(val));
        newNumber = true;
    }

    private void recallMemory() {
        currentValue = memory;
        updateDisplay(fmt(memory));
        newNumber = true;
    }

    // ── Mode toggles ──────────────────────────────────────────────
    private void toggleDegRad() {
        isDegree = !isDegree;
        showBrief("Mode: " + (isDegree ? "DEG" : "RAD"));
    }

    private void toggleInverse() {
        isInverse = !isInverse;
        showBrief(isInverse ? "INV ON" : "INV OFF");
    }

    // ── Display helpers ───────────────────────────────────────────
    private void updateDisplay(String val) {
        displayField.setText(val);
    }

    private String fmt(double v) {
        if (Double.isNaN(v))      return "Error";
        if (Double.isInfinite(v)) return v > 0 ? "∞" : "-∞";
        if (v == Math.floor(v) && Math.abs(v) < 1e15)
            return String.valueOf((long) v);
        return df.format(v);
    }

    private void showError(String msg) {
        expressionField.setText("Error: " + msg);
        displayField.setText("Error");
        currentValue = 0;
        operator = "";
        newNumber = true;
    }

    private void showBrief(String msg) {
        expressionField.setText(msg);
    }
}