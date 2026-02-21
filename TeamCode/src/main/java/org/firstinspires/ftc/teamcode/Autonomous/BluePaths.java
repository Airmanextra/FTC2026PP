package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.BezierCurve;
import com.pedropathing.paths.BezierLine;
import com.pedropathing.paths.PathChain;

public class BluePaths {

    public static class Paths {
        public PathChain Basket1;
        public PathChain Collect1;
        public PathChain Gate1;
        public PathChain Basket2;
        public PathChain Collect2;
        public PathChain Basket3;
        public PathChain Collect3;
        public PathChain Shoot4;
        public PathChain Park;

        public Paths(Follower follower) {
            Basket1 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(56.000, 136.000),
                            new Pose(47.498, 96.334)))
                    .setConstantHeadingInterpolation(Math.toRadians(180))
                    .build();

            Collect1 = follower.pathBuilder()
                    .addPath(new BezierCurve(
                            new Pose(47.498, 96.334),
                            new Pose(39.263, 83.685),
                            new Pose(20.672, 83.725)))
                    .setConstantHeadingInterpolation(Math.toRadians(180))
                    .build();

            Gate1 = follower.pathBuilder()
                    .addPath(new BezierCurve(
                            new Pose(20.672, 83.725),
                            new Pose(28.432, 71.585),
                            new Pose(16.892, 70.913)))
                    .setConstantHeadingInterpolation(Math.toRadians(180))
                    .build();

            Basket2 = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(16.892, 70.913),
                            new Pose(61.547, 72.251)))
                    .setConstantHeadingInterpolation(Math.toRadians(180))
                    .build();

            Collect2 = follower.pathBuilder()
                    .addPath(new BezierCurve(
                            new Pose(61.547, 72.251),
                            new Pose(58.298, 59.760),
                            new Pose(20.770, 59.805)))
                    .setConstantHeadingInterpolation(Math.toRadians(180))
                    .build();

            Basket3 = follower.pathBuilder()
                    .addPath(new BezierCurve(
                            new Pose(20.770, 59.805),
                            new Pose(58.246, 59.770),
                            new Pose(61.240, 72.446)))
                    .setConstantHeadingInterpolation(Math.toRadians(180))
                    .build();

            Collect3 = follower.pathBuilder()
                    .addPath(new BezierCurve(
                            new Pose(61.240, 72.446),
                            new Pose(50.307, 32.611),
                            new Pose(19.638, 35.906)))
                    .setConstantHeadingInterpolation(Math.toRadians(180))
                    .build();

            Shoot4 = follower.pathBuilder()
                    .addPath(new BezierCurve(
                            new Pose(19.638, 35.906),
                            new Pose(50.416, 32.416),
                            new Pose(61.111, 72.411)))
                    .setConstantHeadingInterpolation(Math.toRadians(180))
                    .build();

            Park = follower.pathBuilder()
                    .addPath(new BezierLine(
                            new Pose(61.111, 72.411),
                            new Pose(16.725, 70.913)))
                    .setTangentHeadingInterpolation()
                    .build();
        }
    }
}