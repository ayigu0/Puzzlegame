package ui;

import domain.User;
import ui.LoginJFrame;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.ArrayList;
import java.util.Random;
import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Collections;


public class GameJFrame extends JFrame implements KeyListener, ActionListener {

    private int SCORE_FACTOR = 1204;

    public ArrayList<User> users;
    private User currentuser;

    private JLabel scoreLabel;
    private JLabel preview = new JLabel();
    private JLabel statusLabel;

    private volatile boolean isSolving = false;
    private boolean isScoreAdded;

    private String path = getRandomPath();

    private final String showtotal = "当前用户总分：";
    private final String showstep = "步数: ";

    private int x = 0;
    private int y = 0;

    private int[][] data = new int[4][4];

    private int[][] win = {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 0}};

    int steps = 0;


    JMenuItem replay = new JMenuItem("重新开始 (R)");
    JMenuItem relogin = new JMenuItem("重新登录 (esc)");
    JMenuItem close = new JMenuItem("关闭游戏❌❌");
    JMenuItem account = new JMenuItem("投币支持💰💰");
    JMenuItem solveItem = new JMenuItem("😈😈😈😈 (P)");

    JMenuItem girl = new JMenuItem("女孩👧");
    JMenuItem cat = new JMenuItem("猫猫🐈");
    JMenuItem game = new JMenuItem("游戏🎮");
    JMenuItem land = new JMenuItem("风景🌄");


    public GameJFrame(User user) {

        this.currentuser = user;
        this.users = LoginJFrame.allUsers;
        this.isScoreAdded = false;

        // interface
        initJFrame();

        // menu
        initJMenuBar();

        // init data
        initDataSolvableByShuffle();

        initimage();

        this.setVisible(true);
        this.setResizable(false);
    }


    public GameJFrame(String path) {

        this.users = LoginJFrame.allUsers;

        // interface
        initJFrame();

        // menu
        initJMenuBar();

        // init data
        initDataSolvableByShuffle();

        initimage();

        this.setVisible(true);
        this.setResizable(false);
    }


    private void initDataSolvableByShuffle() {

        int[] arr = new int[16];

        for (int i = 0; i < 16; i++) {
            arr[i] = i;
        }

        Random r = new Random();

        while (true) {

            // Fisher-Yates shuffle
            for (int i = arr.length - 1; i > 0; i--) {

                int j = r.nextInt(i + 1);

                int tmp = arr[i];
                arr[i] = arr[j];
                arr[j] = tmp;
            }

            if (isSolvable(arr)) {
                break;
            }
        }


        // Write values into data and record blank position
        for (int i = 0; i < 16; i++) {

            data[i / 4][i % 4] = arr[i];

            if (arr[i] == 0) {
                x = i / 4;
                y = i % 4;
            }
        }
    }


    private void initimage() {

        // remove everything
        Container cp = this.getContentPane();
        cp.removeAll();


        // add victory image if solved
        if (victory()) {

            JLabel jl = new JLabel(new ImageIcon(LoginJFrame.default_path + "sport/viccc.png"));

            jl.setBounds(190, 283, 220, 95);

            cp.add(jl);

            if (!this.isScoreAdded) {

                currentuser.setScore(this.getTotalscore());

                scoreLabel.setText("当前用户总分：" + currentuser.getScore());

                this.isScoreAdded = true;

                this.saveScore();
            }
        }


        JLabel count = new JLabel(showstep + steps);

        count.setFont(new Font("Chalkboard SE", Font.BOLD, 18));

        count.setForeground(Color.WHITE);

        count.setBounds(50, 30, 100, 20);

        this.getContentPane().add(count);


        // fill in the puzzle pieces
        for (int i = 0; i < 4; i++) {

            for (int j = 0; j < 4; j++) {

                int num = data[i][j];

                ImageIcon icon = new ImageIcon(path + num + ".png");

                JLabel tile = new JLabel(icon);

                tile.setBounds(105 * j + 85, 105 * i + 135, 105, 105);

                tile.setBorder(new BevelBorder(BevelBorder.LOWERED));

                cp.add(tile);
            }
        }


        // background
        ImageIcon back = new ImageIcon(LoginJFrame.default_path + "sport/background.png");

        JLabel background = new JLabel(back);

        background.setBounds(40, 40, 508, 560);

        cp.add(background);


        JLabel previewBg = new JLabel(new ImageIcon(LoginJFrame.default_path + "sport/preview4.png"));

        previewBg.setBounds(565, 100, 450, 480);


        // loading preview
        preview.setBounds(640, 200, 300, 300);

        preview.setHorizontalAlignment(SwingConstants.CENTER);

        preview.setVerticalAlignment(SwingConstants.CENTER);

        preview.setBorder(new BevelBorder(BevelBorder.RAISED));

        cp.add(preview);
        cp.add(scoreLabel);
        cp.add(previewBg);

        updatePreview();


        // solving label
        if (statusLabel != null) {
            cp.add(statusLabel);
        }


        cp.revalidate();
        cp.repaint();
    }


    private void initJMenuBar() {

        JMenuBar jMenuBar = new JMenuBar();

        JMenu function = new JMenu("功能");

        JMenu aboutus = new JMenu("关于我们");

        JMenu switchp = new JMenu("更换图片 (C)");


        replay.addKeyListener(this);

        replay.addActionListener(this);
        relogin.addActionListener(this);

        close.addActionListener(this);
        account.addActionListener(this);

        girl.addActionListener(this);
        cat.addActionListener(this);
        game.addActionListener(this);
        land.addActionListener(this);

        solveItem.addActionListener(this);


        switchp.add(game);
        switchp.add(girl);
        switchp.add(cat);
        switchp.add(land);


        function.add(solveItem);
        function.add(switchp);
        function.add(replay);
        function.add(relogin);
        function.add(close);


        aboutus.add(aboutus);
        aboutus.add(account);


        jMenuBar.add(function);
        jMenuBar.add(aboutus);


        this.setJMenuBar(jMenuBar);
    }


    private void initJFrame() {

        // init frame
        this.setTitle("拼图游戏");

        this.setSize(1024, 750);

        this.setAlwaysOnTop(true);

        this.setDefaultCloseOperation(3);

        this.setLocationRelativeTo(null);

        this.setLayout(null);

        this.addKeyListener(this);


        // score label
        scoreLabel = new JLabel(showtotal + currentuser.getScore());

        scoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));

        scoreLabel.setForeground(Color.WHITE);

        scoreLabel.setBounds(720, 53, 200, 30);


        // background
        Image bgImg = new ImageIcon(LoginJFrame.default_path + "sport/bgbg.png").getImage();

        JPanel root = new BgPanel(bgImg);

        root.setBounds(0, 0, 1024, 750);

        setContentPane(root);

        root.setLayout(null);


        // preview
        preview.setBounds(650, 150, 300, 300);

        preview.setHorizontalAlignment(SwingConstants.CENTER);

        preview.setVerticalAlignment(SwingConstants.CENTER);


        JLabel preview_bg = new JLabel(new ImageIcon(LoginJFrame.default_path + "sport/preview4.png"));

        preview_bg.setBounds(590, 120, 450, 480);


        // solving text
        statusLabel = new JLabel("正在连接peeko的大脑😈😈😈😈😈😈😈😈", SwingConstants.CENTER);

        statusLabel.setFont(statusLabel.getFont().deriveFont(16f));

        statusLabel.setForeground(new java.awt.Color(2, 2, 2));

        statusLabel.setBounds(40, 12, 508, 34);

        statusLabel.setVisible(false);


        this.getContentPane().add(preview_bg);
        this.getContentPane().add(preview);
        this.getContentPane().add(statusLabel);
        this.getContentPane().add(scoreLabel);
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }


    @Override
    public void keyPressed(KeyEvent e) {

    }


    @Override
    public void keyReleased(KeyEvent e) {

        if (isSolving) {
            return;
        }


        int code = e.getKeyCode();

        System.out.println(code);


        if ((code == 37 || code == 65) && !victory()) {

            if (y == 3) {
                return;
            }

            System.out.println("move left");

            data[x][y] = data[x][y + 1];

            data[x][y + 1] = 0;

            y++;

            steps++;

            initimage();


        } else if ((code == 38 || code == 87) && !victory()) {

            if (x == 3) {
                return;
            }

            System.out.println("move up");

            data[x][y] = data[x + 1][y];

            data[x + 1][y] = 0;

            x++;

            steps++;

            initimage();


        } else if ((code == 39 || code == 68) && !victory()) {

            if (y == 0) {
                return;
            }

            System.out.println("move right");

            data[x][y] = data[x][y - 1];

            data[x][y - 1] = 0;

            y--;

            steps++;

            initimage();


        } else if ((code == 40 || code == 83) && !victory()) {

            if (x == 0) {
                return;
            }

            data[x][y] = data[x - 1][y];

            data[x - 1][y] = 0;

            x--;

            steps++;

            initimage();


        } else if (code == 10 && !victory()) {

            data = new int[][]{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 0}};

            this.isScoreAdded = false;

            initimage();


        } else if (code == 82) {

            System.out.println("重新开始");

            if (victory()) {

                steps = 0;

                this.path = getRandomPath();

                initDataSolvableByShuffle();

                initimage();

            } else {

                steps = 0;

                initDataSolvableByShuffle();

                initimage();
            }


        } else if (code == 27) {

            System.out.println("重新登录");

            this.setVisible(false);

            new LoginJFrame(users);

            this.dispose();


        } else if (code == 67 && !victory()) {

            System.out.println("换到任意一个图片");

            steps = 0;

            this.path = getRandomPath();

            initimage();


        } else if (code == 80 && !victory()) {

            this.isScoreAdded = false;

            solveAndPlay();
        }
    }


    // ==============================================================
    // ====================== A* SOLVER ==============================
    // ==============================================================


    private static final int N = 4;

    private Timer playTimer;

    private java.util.List<Character> currentSolution;


    /**
     * Check whether a flattened 15-puzzle state is solvable.
     */
    private boolean isSolvable(int[] s) {

        int inv = 0;

        for (int i = 0; i < s.length; i++) {

            if (s[i] == 0) {
                continue;
            }

            for (int j = i + 1; j < s.length; j++) {

                if (s[j] == 0) {
                    continue;
                }

                if (s[i] > s[j]) {
                    inv++;
                }
            }
        }


        int zeroIdx = 0;

        for (int i = 0; i < s.length; i++) {

            if (s[i] == 0) {

                zeroIdx = i;

                break;
            }
        }


        int zeroRowFromTop = zeroIdx / N;

        int zeroRowFromBottom = N - zeroRowFromTop;


        return ((inv + zeroRowFromBottom) % 2 == 1);
    }


    private static final int[] GOAL = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0};


    private final int[] gRow = new int[16];

    private final int[] gCol = new int[16];


    {
        for (int v = 1; v <= 15; v++) {

            int idx = v - 1;

            gRow[v] = idx / 4;

            gCol[v] = idx % 4;
        }
    }


    /**
     * Convert the board into a one-dimensional byte array.
     */
    private byte[] flattenByte(int[][] grid) {

        byte[] s = new byte[16];

        int k = 0;

        for (int i = 0; i < 4; i++) {

            for (int j = 0; j < 4; j++) {

                s[k++] = (byte) grid[i][j];
            }
        }

        return s;
    }


    /**
     * Manhattan distance + linear conflict heuristic.
     */
    private int heuristic(byte[] s) {

        int h = 0;


        // Manhattan distance
        for (int i = 0; i < 16; i++) {

            int v = s[i] & 0xFF;

            if (v == 0) {
                continue;
            }

            int r = i / 4;

            int c = i % 4;


            h += Math.abs(r - gRow[v]);

            h += Math.abs(c - gCol[v]);
        }


        // Linear conflict: rows
        for (int r = 0; r < 4; r++) {

            int base = r * 4;

            int add = 0;


            for (int i = 0; i < 4; i++) {

                int vi = s[base + i] & 0xFF;


                if (vi == 0 || gRow[vi] != r) {

                    continue;
                }


                for (int j = i + 1; j < 4; j++) {

                    int vj = s[base + j] & 0xFF;


                    if (vj == 0 || gRow[vj] != r) {

                        continue;
                    }


                    if (gCol[vi] > gCol[vj]) {

                        add += 2;
                    }
                }
            }

            h += add;
        }


        // Linear conflict: columns
        for (int c = 0; c < 4; c++) {

            int add = 0;


            for (int i = 0; i < 4; i++) {

                int vi = s[i * 4 + c] & 0xFF;


                if (vi == 0 || gCol[vi] != c) {

                    continue;
                }


                for (int j = i + 1; j < 4; j++) {

                    int vj = s[j * 4 + c] & 0xFF;


                    if (vj == 0 || gCol[vj] != c) {

                        continue;
                    }


                    if (gRow[vi] > gRow[vj]) {

                        add += 2;
                    }
                }
            }

            h += add;
        }


        return h;
    }


    /**
     * Node used by A*.
     */
    private static class AStarNode {

        byte[] state;

        int zeroIndex;

        int g;

        int h;

        AStarNode parent;

        char move;


        AStarNode(byte[] state, int zeroIndex, int g, int h, AStarNode parent, char move) {

            this.state = state;

            this.zeroIndex = zeroIndex;

            this.g = g;

            this.h = h;

            this.parent = parent;

            this.move = move;
        }


        int f() {
            return g + h;
        }
    }


    /**
     * Encode the entire puzzle state into one long value.
     * <p>
     * Each tile value is from 0 to 15 and therefore needs
     * only four bits.
     */
    private long encodeState(byte[] state) {

        long result = 0L;


        for (int i = 0; i < 16; i++) {

            long value = state[i] & 0x0F;

            result |= value << (i * 4);
        }


        return result;
    }


    /**
     * Return true if the given state is the goal state.
     */
    private boolean isGoalState(byte[] state) {

        for (int i = 0; i < 16; i++) {

            if ((state[i] & 0xFF) != GOAL[i]) {

                return false;
            }
        }

        return true;
    }


    /**
     * Reconstruct the solution from the goal node.
     */
    private java.util.List<Character> reconstructPath(AStarNode goalNode) {

        ArrayList<Character> result = new ArrayList<>();


        AStarNode curr = goalNode;


        while (curr.parent != null) {

            result.add(curr.move);

            curr = curr.parent;
        }


        Collections.reverse(result);


        return result;
    }


    /**
     * Solve the 15-puzzle using A* search.
     * <p>
     * f(n) = g(n) + h(n)
     * <p>
     * g(n) is the number of moves from the initial state.
     * h(n) is Manhattan distance + linear conflict.
     */
    private java.util.List<Character> solve15PuzzleAStar(byte[] start) {

        // Already solved
        if (isGoalState(start)) {

            return new ArrayList<>();
        }


        // Find blank position
        int zeroIndex = 0;

        for (int i = 0; i < 16; i++) {

            if (start[i] == 0) {

                zeroIndex = i;

                break;
            }
        }


        /*
         * Priority:
         *
         * 1. smaller f = g + h
         * 2. if tied, smaller h
         */
        PriorityQueue<AStarNode> open = new PriorityQueue<>(Comparator.comparingInt(AStarNode::f).thenComparingInt(node -> node.h));


        /*
         * bestG maps:
         *
         * state -> cheapest path cost discovered
         */
        HashMap<Long, Integer> bestG = new HashMap<>();


        byte[] initialState = start.clone();


        AStarNode startNode = new AStarNode(initialState, zeroIndex, 0, heuristic(initialState), null, '\0');


        open.add(startNode);


        bestG.put(encodeState(initialState), 0);


        // Possible blank movements
        final int[] dr = {0, 0, -1, 1};

        final int[] dc = {-1, 1, 0, 0};

        final char[] move = {'L', 'R', 'U', 'D'};


        while (!open.isEmpty()) {

            // State with minimum f
            AStarNode curr = open.poll();


            long currKey = encodeState(curr.state);


            /*
             * Ignore an outdated copy if a cheaper path
             * to this state has already been found.
             */
            if (curr.g > bestG.getOrDefault(currKey, Integer.MAX_VALUE)) {

                continue;
            }


            // Goal reached
            if (isGoalState(curr.state)) {

                return reconstructPath(curr);
            }


            int zr = curr.zeroIndex / 4;

            int zc = curr.zeroIndex % 4;


            // Generate successor states
            for (int k = 0; k < 4; k++) {

                int nr = zr + dr[k];

                int nc = zc + dc[k];


                // Invalid movement
                if (nr < 0 || nr >= 4 || nc < 0 || nc >= 4) {

                    continue;
                }


                int newZeroIndex = nr * 4 + nc;


                // Copy current board
                byte[] nextState = curr.state.clone();


                // Swap blank and neighboring tile
                byte temp = nextState[curr.zeroIndex];

                nextState[curr.zeroIndex] = nextState[newZeroIndex];

                nextState[newZeroIndex] = temp;


                int newG = curr.g + 1;


                long nextKey = encodeState(nextState);


                /*
                 * Add if:
                 *
                 * - state never seen before, or
                 * - this path reaches it with a lower g
                 */
                if (newG < bestG.getOrDefault(nextKey, Integer.MAX_VALUE)) {


                    bestG.put(nextKey, newG);


                    int newH = heuristic(nextState);


                    AStarNode nextNode = new AStarNode(nextState, newZeroIndex, newG, newH, curr, move[k]);


                    open.add(nextNode);
                }
            }
        }


        // No solution
        return new ArrayList<>();
    }


    /**
     * Solve the current puzzle in a background thread
     * and automatically play the solution.
     */
    private void solveAndPlay() {

        // Flatten current board
        byte[] start = flattenByte(data);


        // Show solving message
        setSolving(true);


        new SwingWorker<java.util.List<Character>, Void>() {


            @Override
            protected java.util.List<Character> doInBackground() {

                return solve15PuzzleAStar(start);
            }


            @Override
            protected void done() {

                try {

                    currentSolution = get();


                    if (playTimer != null && playTimer.isRunning()) {

                        playTimer.stop();
                    }


                    final int[] idx = {0};


                    playTimer = new Timer(60, new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {

                            if (idx[0] >= currentSolution.size()) {

                                playTimer.stop();

                                setSolving(false);

                                initimage();

                                return;
                            }


                            char m = currentSolution.get(idx[0]++);


                            stepMoveByBlank(m);


                            initimage();
                        }
                    });


                    playTimer.start();

                    initimage();


                } catch (Exception ex) {

                    ex.printStackTrace();

                    setSolving(false);

                    JOptionPane.showMessageDialog(GameJFrame.this, "求解失败：" + ex.getMessage());
                }
            }

        }.execute();
    }


    /**
     * Perform one movement of the blank tile.
     */
    private void stepMoveByBlank(char m) {

        switch (m) {

            case 'L':

                if (y > 0) {

                    data[x][y] = data[x][y - 1];

                    data[x][y - 1] = 0;

                    y--;
                }

                break;


            case 'R':

                if (y < N - 1) {

                    data[x][y] = data[x][y + 1];

                    data[x][y + 1] = 0;

                    y++;
                }

                break;


            case 'U':

                if (x > 0) {

                    data[x][y] = data[x - 1][y];

                    data[x - 1][y] = 0;

                    x--;
                }

                break;


            case 'D':

                if (x < N - 1) {

                    data[x][y] = data[x + 1][y];

                    data[x + 1][y] = 0;

                    x++;
                }

                break;
        }
    }


    // ==============================================================
    // ===================== END A* SOLVER ===========================
    // ==============================================================


    public boolean victory() {

        for (int i = 0; i < this.data.length; i++) {

            for (int j = 0; j < data[i].length; j++) {

                if (this.data[i][j] != this.win[i][j]) {

                    return false;
                }
            }
        }

        return true;
    }


    private void setSolving(boolean solving) {

        isSolving = solving;


        if (statusLabel != null) {

            statusLabel.setVisible(solving);

            statusLabel.repaint();
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();


        if (source == relogin) {

            System.out.println("重新登录");

            new LoginJFrame(this.users);

            this.setVisible(false);

            this.dispose();


        } else if (source == solveItem) {

            System.out.println("自动求解");

            this.isScoreAdded = false;

            solveAndPlay();


        } else if (source == replay) {

            System.out.println("重新开始");

            this.isScoreAdded = false;

            steps = 0;

            initDataSolvableByShuffle();

            this.path = getRandomPath();

            initimage();


        } else if (source == close) {

            System.out.println("关闭游戏");

            System.exit(0);


        } else if (source == account) {

            System.out.println("公众号");


            JDialog window1 = new JDialog();


            JLabel jLabel = new JLabel(new ImageIcon(LoginJFrame.default_path + "sport/QR.png"));


            jLabel.setBounds(0, 0, 258, 258);


            window1.getContentPane().add(jLabel);


            window1.setSize(344, 344);

            window1.setAlwaysOnTop(true);

            window1.setLocationRelativeTo(null);

            window1.setModal(true);

            window1.setVisible(true);


        } else if (source == cat && !victory()) {

            System.out.println("换成猫猫");

            int rindex = getRandomindex();

            this.path = LoginJFrame.default_path + "cat" + rindex + "/";

            steps = 0;

            initimage();


        } else if (source == girl && !victory()) {

            int rindex = getRandomindex();

            this.path = LoginJFrame.default_path + "girls" + rindex + "/";

            steps = 0;

            initimage();

            System.out.println("换成可爱的女孩");


        } else if (source == game && !victory()) {

            System.out.println("换成游戏图片");

            int rindex = getRandomindex();

            this.path = LoginJFrame.default_path + "game" + rindex + "/";

            steps = 0;

            initimage();


        } else if (source == land && !victory()) {

            System.out.println("换成风景");

            int rindex = getRandomindex();

            this.path = LoginJFrame.default_path + "lands" + rindex + "/";

            steps = 0;

            initimage();
        }
    }


    private int getRandomindex() {

        String c = this.path.substring(this.path.length() - 2, this.path.length() - 1);


        int curr_index = Integer.parseInt(c);


        ArrayList<Integer> arr = new ArrayList<>();


        for (int i = 1; i < 4; i++) {

            if (i != curr_index) {

                arr.add(i);
            }
        }


        int r = new Random().nextInt(arr.size());


        return arr.get(r);
    }


    private String getRandomPath() {

        // returns a random png path except current one

        String s = LoginJFrame.default_path;


        ArrayList<String> category = new ArrayList<>();


        for (int i = 1; i < 4; i++) {

            category.add("cat" + i + "/");

            category.add("game" + i + "/");

            category.add("girls" + i + "/");

            category.add("lands" + i + "/");
        }


        while (true) {

            int len = category.size();


            int randomindex = new Random().nextInt(len);


            String result = s + category.get(randomindex);


            if (!result.equals(this.path)) {

                return result;
            }
        }
    }


    private int getTotalscore() {

        int result;


        if (currentuser.getUsername().contains("c") || currentuser.getUsername().contains("C")) {


            result = currentuser.getScore() + this.SCORE_FACTOR * (steps + 3);


        } else {

            result = currentuser.getScore() + 15 + Math.max(0, 130 - steps);
        }


        return result;
    }


    private void updatePreview() {

        String fullPath = path + "all.png";


        ImageIcon icon = new ImageIcon(fullPath);


        if (icon.getIconWidth() > 0) {

            Image scaled = icon.getImage().getScaledInstance(preview.getWidth(), preview.getHeight(), Image.SCALE_SMOOTH);


            preview.setIcon(new ImageIcon(scaled));


        } else {

            preview.setIcon(null);
        }
    }


    class BgPanel extends JPanel {

        private Image bg;


        BgPanel(Image bg) {

            setLayout(null);

            this.bg = bg;
        }


        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        }
    }


    private void saveScore() {

        LoginJFrame.dataAccess.save();
    }
}