'use strict';

angular.module('civSim.worlds', ['ngRoute'])

    .directive('float', function () {
        return {
            require: 'ngModel',
            link: function (scope, ele, attr, ctrl) {
                ctrl.$parsers.unshift(function (viewValue) {
                    return parseFloat(viewValue);
                });
            }
        };
    })

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/worlds', {
            templateUrl: 'worlds/worlds.html',
            controller: 'WorldsController'
        });
    }])

    //11.ARCTIC (big / v. cold) #cceeff
    // 10.TUNDRA (big / cold) #fff2cc
    // 4.FOREST (big / mid + cold) #008000
    // 1.GRASSLANDS (medium / mid)    #00ff00
    // 9.SWAMP (small / rather mid but sometimes hot) #734d26
    // 5.STEPPES (big / almost everywhere) #8a996d
    // 8.JUNGLE (big / hot) #00994d
    // 7.ARID LANDS (medium / hot above deserts) #ccbe00
    // 2.DESERTS (big / v. hot) #fff01a
    //
    //
    // 3.MOUNTAINS (medium - lines  / everywhere) #889091
    // 6.PEAKS (small / mountains) #000000
    //
    //
    // water = 0; #0f00e6
    // hasRiver += 12; // yet to be implemented

    .controller('WorldsController', ['$scope', '$http', '$interval', function ($scope, $http, $interval) {
        $http.get("/getWorlds").success(function (r) {
            $scope.worlds = r;
        });

        $scope.colorMap = {
            0: '#0f00e6',
            1: '#00ff00',
            2: '#fff01a',
            3: '#889091',
            4: '#008000',
            5: '#8a996d',
            6: '#000000',
            7: '#ccbe00',
            8: '#00F9Cd',
            9: '#724d26',
            10: '#fff2cc'
        };

        $http.get("/getPropertyValues", {params: {name: "terrain"}}).success(function (r) {
            $scope.colorMap = r;
            $scope.colorMap[0].color = "#0f00e6";
            $scope.colorMap[0].name = "Water";
        });

        $scope.worldRefresh = $interval(function () {
                $http.get("/getWorlds").success(function (r) {
                    $scope.worlds = r;
                });
            },
            2000);

        $scope.singleBoxSize = 15;

        $scope.setBoxSize = function (size) {
            $scope.singleBoxSize = size;
            var r = $scope.opened;
            $scope.generateMap(r.worldMap, r.x, r.y);
        };

        var getMousePos = function (canvas, evt) {
            var rect = canvas.getBoundingClientRect(), // abs. size of element
                scaleX = canvas.width / rect.width,    // relationship bitmap vs. element for X
                scaleY = canvas.height / rect.height;  // relationship bitmap vs. element for Y

            return {
                x: (evt.clientX - rect.left) * scaleX,   // scale mouse coordinates after they have
                y: (evt.clientY - rect.top) * scaleY     // been adjusted to be relative to element
            }
        };

        $scope.yScale = 60;


        var setScopeOnMouseMovement = function (mousePos) {
            $scope.mouseOverCanvas = true;
            $scope.selX = Math.floor(mousePos.x / $scope.singleBoxSize);
            $scope.selY = Math.floor(mousePos.y / $scope.singleBoxSize);

            if ($scope.selX >= $scope.opened.x || $scope.selY >= $scope.opened.y) {
                $scope.mouseOverCanvas = false;
                return;
            }

            var mapX = $scope.opened.x * $scope.singleBoxSize;
            var mapY = $scope.opened.y * $scope.singleBoxSize;

            $scope.selColor = $scope.colorMap[$scope.opened.worldMap[$scope.selX][$scope.selY]].name;
            $scope.detailsX = mousePos.x + $scope.singleBoxSize > mapX - 200 ? mousePos.x - 200 : mousePos.x + $scope.singleBoxSize;
            $scope.detailsY = mousePos.y + $scope.singleBoxSize > mapY - $scope.yScale ? mousePos.y - $scope.yScale : mousePos.y + $scope.singleBoxSize;
        };


        $scope.generateMap = function (mapMatrix, x, y) {
            var canvas = document.getElementById("mapCanvas");
            var ctx = canvas.getContext("2d");

            // canvas.width  = window.innerWidth - 24;
            canvas.width = $scope.singleBoxSize * x;
            canvas.height = $scope.singleBoxSize * y + 120;


            // fillRect(x,y,width,height)
            for (var i = 0; i < x; i++) {
                for (var j = 0; j < y; j++) {
                    var val = mapMatrix[i][j];
                    ctx.fillStyle = val > 15 ? "#ff00ff" : $scope.colorMap[val].color; //fallback if value is strange
                    ctx.fillRect(i * $scope.singleBoxSize, j * $scope.singleBoxSize, $scope.singleBoxSize, $scope.singleBoxSize);
                }
            }
        };

        $scope.countDimension = function (startDim, growthFactor, iterations) {
            return startDim * Math.pow(parseFloat(growthFactor), iterations);
        };

        $scope.openWorldCreateModal = function () {
            $('#createWorldModal').modal('open');
        };

        $scope.createWorld = function () {
            console.log($scope.create);
            $http.post("/createWorld", $scope.create?$scope.create:{}).success(function (r) {
                console.log("Ok, created.");
                $('#createWorldModal').modal('close');
            }).error(function (r) {
                console.error(r);
            });
        };

        $scope.mouseMove = function (event) {
            var canvas = document.getElementById("mapCanvas");
            var mousePos = getMousePos(canvas, event);
            setScopeOnMouseMovement(mousePos, $scope.singleBoxSize);
        };

        $scope.mouseLeave = function (event) {
            $scope.mouseOverCanvas = false;
        };

        $scope.setIterations = function () {
            $scope.create.terrainFactor = new Array($scope.create.iterations);
            for (var i = 0; i < $scope.create.terrainFactor.length; i++) {
                $scope.create.terrainFactor[i] = 1;
            }
        };

        $scope.openWorldModal = function (id) {
            $http.get("/viewWorld", {params: {id: id}}).success(function (r) {
                $scope.opened = r;
                $('#worldModal').modal('open');

                $scope.generateMap(r.worldMap, r.x, r.y);
            });
        };

        $scope.stopRefresh = function () {
            if ($scope.worldRefresh) {
                $interval.cancel($scope.worldRefresh);
                $scope.worldRefresh = undefined;
            }
        };


    }]);