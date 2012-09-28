package com.pmon.clientmultiserver;

import java.util.HashMap;

public class MazeDataCompressor {
	
	public volatile HashMap<Integer, Integer> mazeMap = new HashMap<Integer,Integer>();
    public volatile HashMap<Integer,Integer> rowMap = new HashMap<Integer,Integer>();
    
	public MazeDataCompressor()
	{
		//row1
    	mazeMap.put(101, mask1_1);mazeMap.put(201, mask2_1);mazeMap.put(301, mask3_1);mazeMap.put(401, mask4_1);mazeMap.put(601, mask6_1);mazeMap.put(701, mask7_1);
    	mazeMap.put(801, mask8_1);mazeMap.put(1001, mask10_1);mazeMap.put(1101, mask11_1);mazeMap.put(1201, mask12_1);mazeMap.put(1301, mask13_1);
    	
    	//row2
    	mazeMap.put(102, mask1_2);mazeMap.put(402, mask4_2);mazeMap.put(602, mask6_2);mazeMap.put(802, mask8_2);mazeMap.put(1002, mask10_2);mazeMap.put(1302, mask13_2);
    	//row3
    	mazeMap.put(103, mask1_3);mazeMap.put(203, mask2_3);mazeMap.put(303, mask3_3);mazeMap.put(403, mask4_3);mazeMap.put(503, mask5_3);mazeMap.put(603, mask6_3);
    	mazeMap.put(703, mask7_3);mazeMap.put(803, mask8_3);mazeMap.put(903, mask9_3);mazeMap.put(1003, mask10_3);mazeMap.put(1103, mask11_3);mazeMap.put(1203, mask12_3);
    	mazeMap.put(1303, mask13_3);
    	//row4
    	mazeMap.put(204, mask2_4);mazeMap.put(704, mask7_4);mazeMap.put(1204, mask12_4);
    	//row5
    	mazeMap.put(205, mask2_5);mazeMap.put(305, mask3_5);mazeMap.put(405, mask4_5);mazeMap.put(505, mask5_5);mazeMap.put(605, mask6_5);mazeMap.put(705, mask7_5);
    	mazeMap.put(805, mask8_5);mazeMap.put(905, mask9_5);mazeMap.put(1005, mask10_5);mazeMap.put(1105, mask11_5);mazeMap.put(1205, mask12_5);
    	//row6
    	mazeMap.put(206, mask2_6);mazeMap.put(506, mask5_6);mazeMap.put(906, mask9_6);mazeMap.put(1206, mask12_6);
    	//row7
    	mazeMap.put(107, mask1_7);mazeMap.put(207, mask2_7);mazeMap.put(1207, mask12_7);mazeMap.put(1307, mask13_7);
    	//row8
    	mazeMap.put(108, mask1_8);mazeMap.put(1308, mask13_8);
    	//row9
    	mazeMap.put(109, mask1_9);mazeMap.put(1309, mask13_9);
    	//row10
    	mazeMap.put(110, mask1_10);mazeMap.put(1310, mask13_10);
    	//row11
    	mazeMap.put(111, mask1_11);mazeMap.put(211, mask2_11);mazeMap.put(1211, mask12_11);mazeMap.put(1311, mask13_11);
    	//row12
    	mazeMap.put(212, mask2_12);mazeMap.put(512, mask5_12);mazeMap.put(912, mask9_12);mazeMap.put(1212, mask12_12);
    	//row13
    	mazeMap.put(213, mask2_13);mazeMap.put(313, mask3_13);mazeMap.put(413, mask4_13);mazeMap.put(513, mask5_13);mazeMap.put(613, mask6_13);mazeMap.put(713, mask7_13);
    	mazeMap.put(813, mask8_13);mazeMap.put(913, mask9_13);mazeMap.put(1013, mask10_13);mazeMap.put(1113, mask11_13);mazeMap.put(1213, mask12_13);
    	//row14
    	mazeMap.put(214, mask2_14);mazeMap.put(714, mask7_14);mazeMap.put(1214, mask12_14);
    	//row15
    	mazeMap.put(215, mask2_15);mazeMap.put(415, mask4_15);mazeMap.put(515, mask5_15);mazeMap.put(615, mask6_15);mazeMap.put(715, mask7_15);mazeMap.put(815, mask8_15);
    	mazeMap.put(915, mask9_15);mazeMap.put(1015, mask10_15);mazeMap.put(1215, mask12_15);
    	//row16
    	mazeMap.put(116, mask1_16);mazeMap.put(216, mask2_16);mazeMap.put(316, mask3_16);mazeMap.put(416, mask4_16);mazeMap.put(716, mask7_16);mazeMap.put(1016, mask10_16);
    	mazeMap.put(1116, mask11_16);mazeMap.put(1216, mask12_16);mazeMap.put(1316, mask13_16);
    	//row17
    	mazeMap.put(117, mask1_17);mazeMap.put(717, mask7_17);mazeMap.put(1317, mask13_17);
    	//row18
    	mazeMap.put(118, mask1_18);mazeMap.put(218, mask2_18);mazeMap.put(318, mask3_18);mazeMap.put(418, mask4_18);mazeMap.put(518, mask5_18);mazeMap.put(618, mask6_18);
    	mazeMap.put(718, mask7_18);mazeMap.put(818, mask8_18);mazeMap.put(918, mask9_18);mazeMap.put(1018, mask10_18);mazeMap.put(1118, mask11_18);mazeMap.put(1218, mask12_18);
    	mazeMap.put(1318, mask13_18);
    	//row19
    	mazeMap.put(119, mask1_19);mazeMap.put(419, mask4_19);mazeMap.put(619, mask6_19);mazeMap.put(819, mask8_19);mazeMap.put(1019, mask10_19);mazeMap.put(1319, mask13_19);
    	//row20
    	mazeMap.put(120, mask1_20);mazeMap.put(220, mask2_20);mazeMap.put(320, mask3_20);mazeMap.put(420, mask4_20);mazeMap.put(620, mask6_20);
    	mazeMap.put(720, mask7_20);mazeMap.put(820, mask8_20);mazeMap.put(1020, mask10_20);mazeMap.put(1120, mask11_20);mazeMap.put(1220, mask12_20);
    	mazeMap.put(1320, mask13_20);
    	
    	rowMap.put(1, 0x1FFF);rowMap.put(2, 0x1FFF);rowMap.put(3, 0x1FFF);rowMap.put(4, 0x1FFF);rowMap.put(5, 0x1FFF);rowMap.put(6, 0x1FFF);
    	rowMap.put(7, 0x1FFF);rowMap.put(8, 0x1FFF);rowMap.put(9, 0x1FFF);rowMap.put(10, 0x1FFF);rowMap.put(11, 0x1FFF);rowMap.put(12, 0x1FFF);rowMap.put(13, 0x1FFF);
    	rowMap.put(14, 0x1FFF);rowMap.put(15, 0x1FFF);rowMap.put(16, 0x1FFF);rowMap.put(17, 0x1FFF);rowMap.put(18, 0x1FFF);rowMap.put(19, 0x1FFF);rowMap.put(20, 0x1FFF);
	}
	
	//row1
    int mask1_1 = 0x0FFF;
    int mask2_1 = 0x17FF;
    int mask3_1 = 0x1BFF;
    int mask4_1 = 0x1DFF;
    int mask6_1 = 0x1F7F;
    int mask7_1 = 0x1FBF;
    int mask8_1 = 0x1FDF;
    int mask10_1 = 0x1FF7;
    int mask11_1 = 0x1FFB;
    int mask12_1 = 0x1FFD;
    int mask13_1 = 0x1FFFE;
    
    //row2
    int mask1_2 = 0x0FFF;
    int mask4_2 = 0x1DFF;
    int mask6_2 = 0x1F7F;
    int mask8_2 = 0x1FDF;
    int mask10_2 = 0x1FF7;
    int mask13_2 = 0x1FFE;
   
    //row3
    int mask1_3 = 0x0FFF;
    int mask2_3 = 0x17FF;
    int mask3_3 = 0x1BFF;
    int mask4_3 = 0x1DFF;
    int mask5_3 = 0x1EFF;
    int mask6_3 = 0x1F7F;
    int mask7_3 = 0x1FBF;
    int mask8_3 = 0x1FDF;
    int mask9_3 = 0x1FEF;
    int mask10_3 = 0x1FF7;
    int mask11_3 = 0x1FFB;
    int mask12_3 = 0x1FFD;
    int mask13_3 = 0x1FFE;
    
    //row 4
    int mask2_4 = 0x17FF;
    int mask7_4 = 0x1FBF;
    int mask12_4 = 0x1FFD;
    
    //row 5
    int mask2_5 = 0x17FF;
    int mask3_5 = 0x1BFF;
    int mask4_5 = 0x1DFF;
    int mask5_5 = 0x1EFF;
    int mask6_5 = 0x1F7F;
    int mask7_5 = 0x1FBF;
    int mask8_5 = 0x1FDF;
    int mask9_5 = 0x1FEF;
    int mask10_5 = 0x1FF7;
    int mask11_5 = 0x1FFB;
    int mask12_5 = 0x1FFD;
    
    //row 6
    int mask2_6 = 0x17FF;
    int mask5_6 = 0x1EFF;
    int mask9_6 = 0x1FEF;
    int mask12_6 = 0x1FFD;
    
    //row7
    int mask1_7 = 0x0FFF;
    int mask2_7 = 0x17FF;
    int mask12_7 = 0x1FFD;
    int mask13_7 = 0x1FFE;
    
    //row8
    int mask1_8 = 0x0FFF;
    int mask13_8 = 0x1FFE;
    
    //row9
    int mask1_9 = 0x0FFF;
    int mask13_9 = 0x1FFE;
    
    //row10
    int mask1_10 = 0x0FFF;
    int mask13_10 = 0x1FFE;
    
    //row11
    int mask1_11 = 0x0FFF;
    int mask2_11 = 0x17FF;
    int mask12_11 = 0x1FFD;
    int mask13_11 = 0x1FFE;
    
    //row12
    int mask2_12 = 0x17FF;
    int mask5_12 = 0x1EFF;
    int mask9_12 = 0x1FEF;
    int mask12_12 =0x1FFD;
    
    //row13
    int mask2_13 = 0x17FF;
    int mask3_13 = 0x1BFF;
    int mask4_13 = 0x1DFF;
    int mask5_13 = 0x1EFF;
    int mask6_13 = 0x1F7F;
    int mask7_13 = 0x1FBF;
    int mask8_13 = 0x1FDF;
    int mask9_13 = 0x1FEF;
    int mask10_13 = 0x1FF7;
    int mask11_13 = 0x1FFB;
    int mask12_13 = 0x1FFD;
    
    //row14
    int mask2_14 = 0x17FF;
    int mask7_14 = 0x1FBF;
    int mask12_14 = 0x1FFD;
    
    //row15
    int mask2_15 = 0x17FF;
    int mask4_15 = 0x1DFF;
    int mask5_15 = 0x1EFF;
    int mask6_15 = 0x1F7F;
    int mask7_15 = 0x1FBF;
    int mask8_15 = 0x1FDF;
    int mask9_15 = 0x1FEF;
    int mask10_15 = 0x1FF7;
    int mask12_15 = 0x1FFD;
    
    //row16
    int mask1_16 = 0x0FFF;
    int mask2_16 = 0x17FF;
    int mask3_16 = 0x1BFF;
    int mask4_16 = 0x1DFF;
    int mask7_16 = 0x1FBF;
    int mask10_16 = 0x1FF7;
    int mask11_16 = 0x1FFB;
    int mask12_16 = 0x1FFD;
    int mask13_16 = 0x1FFE;
    
    //row17
    int mask1_17 = 0x0FFF;
    int mask7_17 = 0x1FBF;
    int mask13_17 = 0x1FFE;
    
    //row18
    int mask1_18 = 0x0FFF;
    int mask2_18 = 0x17FF;
    int mask3_18 = 0x1BFF;
    int mask4_18 = 0x1DFF;
    int mask5_18 = 0x1EFF;
    int mask6_18 = 0x1F7F;
    int mask7_18 = 0x1FBF;
    int mask8_18 = 0x1FDF;
    int mask9_18 = 0x1FEF;
    int mask10_18 = 0x1FF7;
    int mask11_18 = 0x1FFB;
    int mask12_18 = 0x1FFD;
    int mask13_18 = 0x1FFE;
    
    //row19
    int mask1_19 = 0x0FFF;
    int mask4_19 = 0x1DFF;
    int mask6_19 = 0x1F7F;
    int mask8_19 = 0x1FDF;
    int mask10_19 = 0x1FF7;
    int mask13_19 = 0x1FFE;
    
    //row20
    int mask1_20 = 0x0FFF;
    int mask2_20 = 0x17FF;
    int mask3_20 = 0x1BFF;
    int mask4_20 = 0x1DFF;
    int mask6_20 = 0x1F7F;
    int mask7_20 = 0x1FBF;
    int mask8_20 = 0x1FDF;
    int mask10_20 = 0x1FF7;
    int mask11_20 = 0x1FFB;
    int mask12_20 = 0x1FFD;
    int mask13_20 = 0x1FFE;
}
