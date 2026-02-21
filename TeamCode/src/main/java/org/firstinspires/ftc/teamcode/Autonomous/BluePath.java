package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.BezierCurve;
import com.pedropathing.paths.BezierLine;
import com.pedropathing.paths.PathChain;

public class BluePaths {

    public final PathChain Path2;
    public final PathChain Collect1;
    public final PathChain Basket1;
    public final PathChain Path4;
    public final PathChain Collect2;
    public final PathChain Basket2;
    public final PathChain Path7;
    public final PathChain Collect3;
    public final PathChain Path9;

    public BluePaths(Follower follower) {
        Path2 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(56.000, 8.000),
                                new Pose(35.725, 36.268)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();

        Collect1 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(35.725, 36.268),
                                new Pose(21.554, 35.530)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();

        Basket1 = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Pose(21.554, 35.530),
                                new Pose(65.657, 65.544),
                                new Pose(34.819, 108.282)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();

        Path4 = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Pose(34.819, 108.282),
                                new Pose(56.019, 80.983),
                                new Pose(35.101, 59.725)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();

        Collect2 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(35.101, 59.725),
                                new Pose(21.805, 59.822)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();

        Basket2 = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Pose(21.805, 59.822),
                                new Pose(64.159, 68.430),
                                new Pose(34.937, 108.286)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();

        Path7 = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Pose(34.937, 108.286),
                                new Pose(52.481, 85.211),
                                new Pose(33.544, 84.192)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();

        Collect3 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(33.544, 84.192),
                                new Pose(21.711, 84.150)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();

        Path9 = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Pose(21.711, 84.150),
                                new Pose(39.721, 106.014),
                                new Pose(34.972, 108.505)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();
    }
}