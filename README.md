# NaviMenu 随便开发的一个支持两个弹出动画的自定义控件。 
实现原理超级简单，
下面贴几个关键代码：

 /**
     * 第一个子View为按钮，为按钮布局且初始化点击事件
     */
    private void layoutButton() {
        View cButton = getChildAt(0);
        cButton.setOnClickListener(this);

        int width = cButton.getMeasuredWidth();
        int height = cButton.getMeasuredHeight();
        int marinBottom = (BottomBarHeight - height) / 2; //居中处理

        int l = getMeasuredWidth() / 2 - (width / 2);
        int t = getMeasuredHeight() - height - marinBottom;
        cButton.layout(l, t, l + width, t + height);
    }
    
    /*****************************************/
     @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            /**
             * 初始化用户点击按钮位置
             */
            layoutButton();

            int count = getChildCount();
            /**
             * 根据需求 设置两个View 的位置
             */
            for (int i = 0; i < count - 1; i++) {
                View child = getChildAt(i + 1);
                child.setVisibility(View.GONE);

                int cWidth = child.getMeasuredWidth();
                int cHeight = child.getMeasuredHeight();

                int C1w = getMeasuredWidth() / 2 - (cWidth / 2) - (cWidth / 2 + cWidth / 4);
                int C1h = getMeasuredHeight() - cHeight - cHeight;

                int C2w = getMeasuredWidth() / 2 - (cWidth / 2) + (cWidth / 2 + cWidth / 4);
                int C2h = getMeasuredHeight() - cHeight - cHeight;

                if (i == 0) {
                    child.layout(C1w, C1h, C1w + cWidth, C1h + cHeight);
                } else if (i == 1) {
                    child.layout(C2w, C2h, C2w + cWidth, C2h + cHeight);
                }
            }
        }
    }
