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

public class GameJFrame extends JFrame implements KeyListener, ActionListener {


    public ArrayList<User> users;
    private User currentuser;

    private JLabel scoreLabel;
    private JLabel preview = new JLabel();
    private JLabel statusLabel;
    private volatile boolean isSolving = false;
    private boolean isScoreAdded = false;
    private String path = getRandomPath();

    private final String showtotal = "当前用户总分：";
    private final String showstep = "步数: ";
    private int x = 0;
    private int y = 0;
    private int[][] data = new int[4][4];
    private int[][] win = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12},
            {13, 14, 15, 0}
    };
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
        //interface
        initJFrame();
        //menu
        initJMenuBar();

        //initdata
        initDataSolvableByShuffle();

        initimage();
        this.setVisible(true);
        this.setResizable(false);
    }

    public GameJFrame(String path) {
        this.users = LoginJFrame.allUsers;
        //interface
        initJFrame();
        //menu
        initJMenuBar();

        //initdata
        initDataSolvableByShuffle();

        initimage();
        this.setVisible(true);
        this.setResizable(false);
    }

    private void initDataSolvableByShuffle() {
        int[] arr = new int[16];
        for (int i = 0; i < 16; i++) arr[i] = i;

        Random r = new Random();
        while (true) {
            // Fisher–Yates 洗牌
            for (int i = arr.length - 1; i > 0; i--) {
                int j = r.nextInt(i + 1);
                int tmp = arr[i];
                arr[i] = arr[j];
                arr[j] = tmp;
            }
            if (isSolvable(arr)) break; // 可解就停止
        }

        // 写回 data，并记录空格坐标 x,y
        for (int i = 0; i < 16; i++) {
            data[i / 4][i % 4] = arr[i];
            if (arr[i] == 0) {
                x = i / 4;
                y = i % 4;
            }
        }
    }//AI

    private void initimage() {
        //remove everything
        Container cp = this.getContentPane();
        cp.removeAll();

        // add victory if the puzzle is solved
        if (victory()) {
            JLabel jl = new JLabel(new ImageIcon(LoginJFrame.default_path + "sport/viccc.png"));
            jl.setBounds(190, 283, 220, 95);
            cp.add(jl);
            if (!this.isScoreAdded) {
                currentuser.setScore(this.getTotalscore());
                scoreLabel.setText("当前用户总分：" + currentuser.getScore());
                this.isScoreAdded = true;
            }
        }
        JLabel count = new JLabel(showstep + steps);
        count.setFont(new Font("Chalkboard SE", Font.BOLD, 18));
        count.setForeground(Color.WHITE);
        count.setBounds(50, 30, 100, 20);
        this.getContentPane().add(count);

        // fill in the small pieces
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

        // loading background
        ImageIcon back = new ImageIcon(LoginJFrame.default_path + "sport/background.png");
        JLabel background = new JLabel(back);
        background.setBounds(40, 40, 508, 560);
        cp.add(background);
        JLabel previewBg = new JLabel(new ImageIcon(
                LoginJFrame.default_path + "sport/preview4.png"
        ));
        previewBg.setBounds(565, 100, 450, 480);

        //loading preview
        preview.setBounds(640, 200, 300, 300);
        preview.setHorizontalAlignment(SwingConstants.CENTER);
        preview.setVerticalAlignment(SwingConstants.CENTER);
        preview.setBorder(new BevelBorder(BevelBorder.RAISED));
        cp.add(preview);
        cp.add(scoreLabel);
        cp.add(previewBg);
        updatePreview();

        //AI advice
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
        // init the basic frame.
        this.setTitle("拼图游戏");
        this.setSize(1024, 750);
        this.setAlwaysOnTop(true);
        this.setDefaultCloseOperation(3);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.addKeyListener(this);

        //add scorelabel
        scoreLabel = new JLabel(showtotal + currentuser.getScore());
        scoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(720, 53, 200, 30);

        // loading background on the main game interface.
        Image bgImg = new ImageIcon(LoginJFrame.default_path + "sport/bgbg.png").getImage();
        JPanel root = new BgPanel(bgImg);
        root.setBounds(0, 0, 1024, 750);
        setContentPane(root);
        root.setLayout(null);
        //setting preview
        preview.setBounds(650, 150, 300, 300);
        preview.setHorizontalAlignment(SwingConstants.CENTER);
        preview.setVerticalAlignment(SwingConstants.CENTER);
        JLabel preview_bg = new JLabel(new ImageIcon(LoginJFrame.default_path + "sport/preview4.png"));
        preview_bg.setBounds(590, 120, 450, 480);

        //setting solving words
        statusLabel = new JLabel("正在连接peeko的大脑😈😈😈😈😈😈😈😈", SwingConstants.CENTER);
        statusLabel.setFont(statusLabel.getFont().deriveFont(16f));
        statusLabel.setForeground(new java.awt.Color(2, 2, 2));
        statusLabel.setBounds(40, 12, 508, 34);
        statusLabel.setVisible(false);

        // add everything to the interface.
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
            data = new int[][]{
                    {1, 2, 3, 4},
                    {5, 6, 7, 8},
                    {9, 10, 11, 12},
                    {13, 14, 15, 0}
            };
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

    // from chatgpt========================================================================
    // ================== Solver & Player =================================================


    private static final int N = 4;
    private Timer playTimer;
    private java.util.List<Character> currentSolution;

    private boolean isSolvable(int[] s) {
        int inv = 0;
        for (int i = 0; i < s.length; i++) {
            if (s[i] == 0) continue;
            for (int j = i + 1; j < s.length; j++) {
                if (s[j] == 0) continue;
                if (s[i] > s[j]) inv++;
            }
        }
        // 找到空格（0）所在的“自底向上”的行号（从1开始）
        int zeroIdx = 0;
        for (int i = 0; i < s.length; i++)
            if (s[i] == 0) {
                zeroIdx = i;
                break;
            }
        int zeroRowFromTop = zeroIdx / N;             // 0-based from top
        int zeroRowFromBottom = N - zeroRowFromTop;   // 1..4 from bottom

        // 4x4 规则：空格自底向上为偶数行时，inv 必须为奇数；为奇数行时，inv 必须为偶数
        // 等价写法： (inv + zeroRowFromBottom) % 2 == 1
        return ((inv + zeroRowFromBottom) % 2 == 1);
    }

    private static final int[] GOAL = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0};
    private final int[] gRow = new int[16], gCol = new int[16];

    {
        for (int v = 1; v <= 15; v++) {
            int idx = v - 1;
            gRow[v] = idx / 4;
            gCol[v] = idx % 4;
        }
    }

    private byte[] flattenByte(int[][] grid) {
        byte[] s = new byte[16];
        int k = 0;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                s[k++] = (byte) grid[i][j];
        return s;
    }

    private int heuristic(byte[] s) {
        int h = 0;
        // Manhattan
        for (int i = 0; i < 16; i++) {
            int v = s[i] & 0xFF;
            if (v == 0) continue;
            int r = i / 4, c = i % 4;
            h += Math.abs(r - gRow[v]) + Math.abs(c - gCol[v]);
        }
        // Linear conflict（行）
        for (int r = 0; r < 4; r++) {
            int base = r * 4, add = 0;
            for (int i = 0; i < 4; i++) {
                int vi = s[base + i] & 0xFF;
                if (vi == 0 || gRow[vi] != r) continue;
                for (int j = i + 1; j < 4; j++) {
                    int vj = s[base + j] & 0xFF;
                    if (vj == 0 || gRow[vj] != r) continue;
                    if (gCol[vi] > gCol[vj]) add += 2;
                }
            }
            h += add;
        }
        // Linear conflict（列）
        for (int c = 0; c < 4; c++) {
            int add = 0;
            for (int i = 0; i < 4; i++) {
                int vi = s[i * 4 + c] & 0xFF;
                if (vi == 0 || gCol[vi] != c) continue;
                for (int j = i + 1; j < 4; j++) {
                    int vj = s[j * 4 + c] & 0xFF;
                    if (vj == 0 || gCol[vj] != c) continue;
                    if (gRow[vi] > gRow[vj]) add += 2;
                }
            }
            h += add;
        }
        return h;
    }

    private java.util.List<Character> solve15PuzzleIDA(byte[] start) {
        // 已是目标
        boolean ok = true;
        for (int i = 0; i < 16; i++)
            if ((start[i] & 0xFF) != GOAL[i]) {
                ok = false;
                break;
            }
        if (ok) return new ArrayList<>();

        int zi = 0;
        for (int i = 0; i < 16; i++)
            if (start[i] == 0) {
                zi = i;
                break;
            }

        int bound = heuristic(start);
        char[] path = new char[1280]; // 足够长
        int[] resLen = new int[1];

        while (true) {
            int t = idaDfs(start, zi, 0, bound, '\0', path, resLen);
            if (t == -1) {
                ArrayList<Character> ans = new ArrayList<>(resLen[0]);
                for (int i = 0; i < resLen[0]; i++) ans.add(path[i]);
                return ans;
            }
            bound = t; // 提高阈值继续
        }
    }

    private int idaDfs(byte[] s, int zi, int g, int bound, char last, char[] path, int[] resLen) {
        int h = heuristic(s), f = g + h;
        if (f > bound) return f;

        // 到达目标
        boolean goal = true;
        for (int i = 0; i < 16; i++)
            if ((s[i] & 0xFF) != GOAL[i]) {
                goal = false;
                break;
            }
        if (goal) {
            resLen[0] = g;
            return -1;
        }

        int best = Integer.MAX_VALUE;
        int zr = zi / 4, zc = zi % 4;

        final int[] dr = {0, 0, -1, 1};
        final int[] dc = {-1, 1, 0, 0};
        final char[] mv = {'L', 'R', 'U', 'D'};
        final char[] inv = {'R', 'L', 'D', 'U'}; // 反向剪枝

        for (int k = 0; k < 4; k++) {
            if (last == inv[k]) continue;
            int nr = zr + dr[k], nc = zc + dc[k];
            if (nr < 0 || nr >= 4 || nc < 0 || nc >= 4) continue;

            int nzi = nr * 4 + nc;
            byte tmp = s[zi];
            s[zi] = s[nzi];
            s[nzi] = tmp; // 原地交换

            path[g] = mv[k];
            int t = idaDfs(s, nzi, g + 1, bound, mv[k], path, resLen);

            tmp = s[zi];
            s[zi] = s[nzi];
            s[nzi] = tmp; // 复原

            if (t == -1) return -1;
            if (t < best) best = t;
        }
        return best;
    }

    private void solveAndPlay() {
        // 展平
        byte[] start = flattenByte(data);

        // 正在求解（显示提示并禁用操作）
        setSolving(true);

        new SwingWorker<java.util.List<Character>, Void>() {
            @Override
            protected java.util.List<Character> doInBackground() {
                // 这里在后台线程里执行，UI 不会卡顿
                return solve15PuzzleIDA(start);
            }

            @Override
            protected void done() {
                try {
                    currentSolution = get(); // 拿结果
                    // 播放动画
                    if (playTimer != null && playTimer.isRunning()) playTimer.stop();
                    final int[] idx = {0};
                    playTimer = new Timer(60, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (idx[0] >= currentSolution.size()) {
                                playTimer.stop();
                                setSolving(false);  // 结束后隐藏提示
                                initimage();
                                return;
                            }
                            char m = currentSolution.get(idx[0]++);
                            stepMoveByBlank(m);
                            initimage();
                        }
                    });
                    playTimer.start();
                    initimage(); // 立即重绘一次，显示“正在自动求解…”
                } catch (Exception ex) {
                    ex.printStackTrace();
                    setSolving(false);
                    JOptionPane.showMessageDialog(GameJFrame.this, "求解失败：" + ex.getMessage());
                }
            }
        }.execute();
    }

    private void stepMoveByBlank(char m) {
        // 注意：你现有的 keyReleased 是“方块动到空格处”，等价于“空格向反方向移动”
        // 这里我们直接以“空格朝 L/R/U/D 移动”来更新 data。
        switch (m) {
            case 'L': // 空格向左：与左侧交换 => y-1
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

    //======================================================================================

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
        // returns a random png path excepts the current one.
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
        int result = currentuser.getScore() + 15 - (this.steps / 20);
        if(result < 5){
            return currentuser.getScore() + 5;
        }else{
            return result;
        }
    }

    private void updatePreview() {
        String fullPath = path + "all.png";
        ImageIcon icon = new ImageIcon(fullPath);
        if (icon.getIconWidth() > 0) {
            Image scaled = icon.getImage().getScaledInstance(
                    preview.getWidth(), preview.getHeight(), Image.SCALE_SMOOTH);
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
}


