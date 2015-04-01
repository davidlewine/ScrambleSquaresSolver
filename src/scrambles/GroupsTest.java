/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scrambles;
//an additional comment

import java.util.*;
import java.io.*;

/**
 *
 * @author David
 */
public class GroupsTest {

    private static int[][] distInfo = new int[36][36];
    private static int[][][] sortedData = new int[36][36][2];

    public static void main(String[] args) {
        int[][] dInfo = getDistInfo("kittens.txt");
        ArrayList<Integer>[] initGroups = initialGroups(dInfo);
        ArrayList<ArrayList<Integer>> possGroups = possibleGroups(initGroups);
        ArrayList<ArrayList<Integer>> aGroups = allGroups(possGroups);
        ArrayList<ArrayList<Integer>> fGroups = finalGroups(aGroups);
    }
    
    public static ArrayList<ArrayList<Integer>> getGroups(int[][] dInfo){
        ArrayList<Integer>[] initGroups = initialGroups(dInfo);
        ArrayList<ArrayList<Integer>> possGroups = possibleGroups(initGroups);
        ArrayList<ArrayList<Integer>> aGroups = allGroups(possGroups);
        ArrayList<ArrayList<Integer>> fGroups = finalGroups(aGroups);
        return fGroups;
    }

    public static int[][] getDistInfo(String fileName) {
        int[][] dInfo = new int[36][36];
        String line;
        String edgeNumsLine;
        String[] edgeNumsLineSplit;
        String[] lineSplit;

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"));
            BufferedReader br = new BufferedReader(new FileReader(fileName));

            for (int d = 0; d < 36; d++) {
                line = br.readLine();//read sequence of distances to edge roi's
                if (line != null) {
                    //process next line
                    lineSplit = line.split(",");
                    int[] data = new int[36];
                    for (int i = 0; i < 36; i++) {
                        data[i] = Integer.parseInt(lineSplit[i]);
                    }
                    dInfo[d] = data;
                }
            }

            System.out.println("*********  distInfo  **********");
            for (int[] arr : distInfo) {
                System.out.println(Arrays.toString(arr));
            }

            br.close();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dInfo;
    }

    public static ArrayList<Integer>[] initialGroups(int[][] dInfo) {
        //fileName contains edgeROI comparison scores (sorted?)
        distInfo = dInfo;
        int maxGroupSize = 15;
        int[] edgeNums = new int[36];

        int[][] lineData = new int[36][2];

        double[][] lineDataScored;

        ArrayList<Integer>[] lineGroups = new ArrayList[36];
        double[] maxIndices = new double[36];

        for (int d = 0; d < 36; d++) {

            lineDataScored = new double[36][4];

            lineData = new int[36][2];
            //sort data into lineData[][] consisting of ordered pairs:(edge id num, dist to edge)
            for (int i = 0; i < 36; i++) {
                lineData[i][0] = i;
                lineData[i][1] = distInfo[d][i];
            }
            sort(lineData);
            sortedData[d] = lineData;

            double minDiff = 1000, maxDiff = 0, minError = 1000, maxError = 0, minAngle = 1000, maxAngle = 0;
            //score each point and set max and mins
            for (int i = 1; i <= maxGroupSize; i++) {
                double diff = lineData[i][1] - lineData[i - 1][1];
                if (diff < minDiff) {
                    minDiff = diff;
                }
                if (diff > maxDiff) {
                    maxDiff = diff;
                }
                double error = getError(lineData, i, maxGroupSize);
                if (error < minError) {
                    minError = error;
                }
                if (error > maxError) {
                    maxError = error;
                }
                double angle = getAngle(lineData, i, maxGroupSize);
                if (angle < minAngle) {
                    minAngle = angle;
                }
                if (angle > maxAngle) {
                    maxAngle = angle;
                }
                lineDataScored[i][0] = diff;
                lineDataScored[i][1] = error;
                lineDataScored[i][2] = angle;
                lineDataScored[i][3] = 0;
            }

            //normalize scores
            for (int i = 1; i <= maxGroupSize; i++) {
                lineDataScored[i][0] = (lineDataScored[i][0] - minDiff) / (maxDiff - minDiff);
                lineDataScored[i][1] = (lineDataScored[i][1] - minError) / (maxError - minError);
                lineDataScored[i][2] = (lineDataScored[i][2] - minAngle) / (maxAngle - minAngle);
                lineDataScored[i][3] = lineDataScored[i][0] + (1 - lineDataScored[i][1]) + (1 - lineDataScored[i][2]);
            }

            //get index of data point with highest score
            int maxIndex = 0;
            double maxScore = 0;
            for (int i = 1; i <= maxGroupSize; i++) {
                if (lineDataScored[i][3] > maxScore) {
                    maxScore = lineDataScored[i][3];
                    maxIndex = i;
                }
            }
            maxIndices[d] = maxIndex;
            // create group of first maxIndex edge id numbers and add to lineGroups array
            ArrayList<Integer> group = new ArrayList();
            for (int k = 0; k < maxIndex; k++) {
                group.add(lineData[k][0]);
            }
            System.out.println("group size: " + group.size());
            System.out.println("group: " + Arrays.toString(group.toArray()));
            lineGroups[d] = group;
        }

        System.out.println("lineGroups length: " + lineGroups.length);
        System.out.println("******************");
        for (ArrayList g : lineGroups) {
            System.out.println(Arrays.toString(g.toArray()));
        }

        return lineGroups;
    }

    public static ArrayList<ArrayList<Integer>> possibleGroups(ArrayList<Integer>[] initGroups) {
        ArrayList<ArrayList<Integer>> possibleGroups = new ArrayList();
        for (int i = 0; i < initGroups.length; i++) {
            ArrayList<Integer> group = initGroups[i];
            boolean possible = true;
            for (int j = 1; j < group.size(); j++) {//start at 1 because: for every OTHER member of the group
                ArrayList<Integer> otherGroup = initGroups[group.get(j)];

                for (int k = 0; k < Math.min(otherGroup.size(), group.size()); k++) {
                    //check if the first n spots of the otherGroup are in original group. 
                    if (!group.contains(otherGroup.get(k))) {
                        possible = false;
                        break;
                    }
                }
            }
            if (possible) {
                //check if group is already on list of possible groups
                for (ArrayList<Integer> p : possibleGroups) {
                    if (p.size() == group.size()) {
                        boolean allMatch = true;
                        for (int k = 0; k < group.size(); k++) {
                            if (!p.contains(group.get(k))) {
                                allMatch = false;
                            }
                        }
                        if (allMatch) {
                            possible = false;
                            break;
                        }
                    }
                }
                if (possible) {
                    possibleGroups.add(group);
                }
            }

        }
        System.out.println("possibleGroups");
        for (ArrayList pg : possibleGroups) {
            System.out.println(Arrays.toString(pg.toArray()));
        }
        return possibleGroups;
    }

    public static ArrayList<ArrayList<Integer>> allGroups(ArrayList<ArrayList<Integer>> possGroups) {
        //assert: possGroups contains no duplicate groups
        for (int i = 0; i < possGroups.size() - 1; i++) {
            for (int j = i + 1; j < possGroups.size(); j++) {
                if (subset(possGroups.get(i), possGroups.get(j))) {
                    possGroups.get(j).removeAll(possGroups.get(i));
                } else {
                    if (subset(possGroups.get(j), possGroups.get(i))) {
                        possGroups.get(i).removeAll(possGroups.get(j));
                    }
                }
            }
        }
        //remove any empty groups
        Iterator<ArrayList<Integer>> iter = possGroups.iterator();
        while (iter.hasNext()) {
            if (iter.next().size() == 0) {
                iter.remove();
            }
        }
        System.out.println("allGroups");
        for (ArrayList pg : possGroups) {
            System.out.println(Arrays.toString(pg.toArray()));
        }
        return possGroups;
    }

    public static ArrayList<ArrayList<Integer>> finalGroups(ArrayList<ArrayList<Integer>> groupsOrig) {
        //int mg = 8;
        double minScore = 1000;
        double minIndex = -1;
        double maxError = 0, minError = 1000, maxAngle = 0, minAngle = 1000, maxDiff = 0, minDiff = 1000;
        ArrayList<double[][]> scoreSets = new ArrayList();
        ArrayList<ArrayList<ArrayList<Integer>>> groupsSets = new ArrayList();
        for (int mg = 4; mg < 12; mg++) {
            //make new list groups that is a deep copy of groupsOrig
            ArrayList<ArrayList<Integer>> groups = new ArrayList();
            for (ArrayList<Integer> group : groupsOrig) {
                ArrayList<Integer> groupCopy = new ArrayList();
                for (int n : group) {
                    groupCopy.add(n);
                }
                groups.add(groupCopy);
            }

            while (groups.size() > 8) {
                //find the nearest two groups
                double minDist = 1000;
                int minIndexA = -1;
                int minIndexB = -1;
                for (int i = 0; i < groups.size() - 1; i++) {
                    for (int j = i + 1; j < groups.size(); j++) {
                        if (groups.get(i).size() + groups.get(j).size() < mg) {
                            double d = groupDist(groups.get(i), groups.get(j));
                            if (d < minDist) {
                                minDist = d;
                                minIndexA = i;
                                minIndexB = j;
                            }
                        }
                    }
                }
                //merge the nearest two groups
                (groups.get(minIndexA)).addAll(groups.get(minIndexB));
                groups.remove(minIndexB);
            }
            //score this set of finalGroups
            double[][] scores = new double[groups.size()][4];//groups.size() should = 8 here.
            //double avgError = 0, avgAngle = 0, avgDiff = 0;

            //get raw scores for error, angle and difference
            for (int i = 0; i < groups.size(); i++) {
                double avgError = 0, avgAngle = 0, avgDiff = 0;
                ArrayList<Integer> g = groups.get(i); //g is a group
                for (int n : g) {//n is an edge in group g
                    double gnError = getError(sortedData[n], g.size(), 15);
                        //g.size() is the size of the group the edge belongs to and indicates where the knee should be for
                        // the sorted distance data for the edge.  g.size() should be the first element not included in the group.
                    double gnAngle = getAngle(sortedData[n], g.size(), 15);
                    int fn = sortedData[n][g.size()][1];//fn = distance of edge n to first edge (in sorted edge data) supposedly not in group 
                    int ln = sortedData[n][g.size() - 1][1];// ln = distance of edge n to last edge (in sorted edge data) supposedly in group
                    double gnDiff = Math.abs(fn - ln);
                        //if the group is a correct group, the difference in distances should be big because fn will be
                        // the distance to an edge in a different group, and fl will be the distance to an edge (the most different) in the same group
                    if (gnError > maxError) {
                        maxError = gnError;
                    }
                    if (gnError < minError) {
                        minError = gnError;
                    }
                    if (gnAngle > maxAngle) {
                        maxAngle = gnAngle;
                    }
                    if (gnAngle < minAngle) {
                        minAngle = gnAngle;
                    }
                    if (gnDiff > maxDiff) {
                        maxDiff = gnDiff;
                    }
                    if (gnDiff < minDiff) {
                        minDiff = gnDiff;
                    }
                    //gnError/Angle/Diff are scores representing how good the grouping is relative to edge n and its sorted data.
                    avgError += gnError;
                    avgAngle += gnAngle;
                    avgDiff += gnDiff;
                }
                avgError /= g.size();
                avgAngle /= g.size();
                avgDiff /= g.size();
                scores[i][0] = avgDiff;
                scores[i][1] = avgError;
                scores[i][2] = avgAngle;
                //scores has diff, error, and angle scores indicating an average of how good a grouping  group i is relative to each member of
                //group i's sorted data of distances to other edges in group i.
            }
            scoreSets.add(scores); //scoreSets contains a set of score data for each set of groups calcuated for 
                                    //each different choice of maximum group size.  A set of score data is the average of the scores for each member of a group
            groupsSets.add(groups);//groupsSets contains the corresponding set of groups
        }
        //normalize scores (based on max and min score values) and sum the normalized scores 
        //of each set of groups corresponding to a different maximum group size.
        
        double maxTotalScore = 0;
        int indexOfBestGroupSet = -1;

        for (int i = 0; i < groupsSets.size(); i++) {//for each set of final groups corresponding to a different max group size
            double[][] scoreSet = scoreSets.get(i);
            double totalScore = 0;
            for (double[] s : scoreSet) {//for each group in the set, set item 3 in the score set to the sum of the normalized scores for
                                         //error, angle, and diff held in items 0 - 2.
                s[3] = (s[0] - minDiff) / (maxDiff - minDiff) + (1 - (s[1] - minError) / (maxError - minError))
                        + (1 - (s[2] - minAngle) / (maxAngle - minAngle));
                totalScore += s[3];
            }
            if (totalScore > maxTotalScore) {
                maxTotalScore = totalScore;
                indexOfBestGroupSet = i;
            }
        }
        ArrayList<ArrayList<Integer>> bestGroupSet = groupsSets.get(indexOfBestGroupSet);

        System.out.println("finalGroups");
        for (ArrayList g : bestGroupSet) {
            System.out.println(Arrays.toString(g.toArray()));
        }
        return bestGroupSet;
    }

    private static double groupDist(ArrayList<Integer> groupA, ArrayList<Integer> groupB) {
        double distSum = 0;
        for (int i = 0; i < groupA.size(); i++) {
            for (int j = 0; j < groupB.size(); j++) {
                int p = groupA.get(i);
                int q = groupB.get(j);
                distSum += distInfo[p][q];
            }
        }
        return distSum / (groupA.size() * groupB.size());
    }

    private static boolean subset(ArrayList listA, ArrayList listB) {
        boolean result = true;
        if (listB.size() >= listA.size()) {
            for (int i = 0; i < listA.size(); i++) {
                if (!listB.contains(listA.get(i))) {
                    result = false;
                    break;
                }
            }
        } else {
            result = false;
        }
        return result;
    }

    public static double getError(int[][] data, int pos, int maxPos) {

        double ay = data[0][1], ax = 0, by = data[pos][1], bx = pos, cy = data[maxPos][1], cx = maxPos;

        double slopeAB = (by - ay) / (bx - ax);
        double interceptAB = ay;
        double errorAB = 0;
        double slopeBC = (cy - by) / (cx - bx);
        double interceptBC = by - slopeBC * pos;
        double errorBC = 0;
        for (int x = 0; x <= pos; x++) {
            errorAB += Math.abs((slopeAB * x + interceptAB) - data[x][1]);
        }
        for (int x = pos; x <= maxPos; x++) {
            errorBC += Math.abs((slopeBC * x + interceptBC) - data[x][1]);
        }
        return errorAB + errorBC;
    }

    private static double getAngle(int[][] data, int pos, int maxPos) {
        double ay = data[0][1], ax = 0, by = data[pos][1], bx = pos, cy = data[maxPos][1], cx = maxPos;
        double angle = Math.atan((bx - ax) / (by - ay)) + Math.atan((cy - by) / (cx - bx));

        return angle;
    }

    private static void sort(int[][] neighborInfo) {
        for (int i = 1; i < neighborInfo.length; i++) {
            int place = i - 1;
            int[] temp = neighborInfo[i];
            while (place >= 0 && neighborInfo[place][1] > temp[1]) {
                neighborInfo[place + 1] = neighborInfo[place];
                place--;
            }
            neighborInfo[place + 1] = temp;
        }
    }

}
