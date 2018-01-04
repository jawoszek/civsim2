'use strict';

angular.module('civSim.simulations', ['ngRoute'])

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
        $routeProvider.when('/simulations', {
            templateUrl: 'simulations/simulations.html',
            controller: 'SimulationsController'
        });
    }])

    .controller('SimulationsController', ['$scope', '$http', '$interval', '$timeout', function ($scope, $http, $interval, $timeout) {
        $scope.frames = new Map();
        $scope.currentFrameDBID = 0;
        $scope.currentFrame = [];
        $scope.provincesMap = new Map();
        $scope.singleBoxSize = 15;

        var handleSimulations = function (r) {
            $scope.done = r.done;
            $scope.inProgress = r.inProgress;
            $scope.waiting = r.waiting;
        };

        $http.get("/getSimulations").success(function (r) {
            handleSimulations(r)
        });

        $scope.simRefresh = $interval(function () {
                $http.get("/getSimulations")
                    .success(function (r) {
                        handleSimulations(r)
                    })
            },
            2000);


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


        $scope.setBoxSize = function (size) {
            $scope.singleBoxSize = size;

            $scope.generateMap($scope.opened.map.worldMap,
                $scope.opened.map.x,
                $scope.opened.map.y);

            $scope.renderFrame($scope.currentFrame);
        };

        $scope.generateMap = function (mapMatrix, x, y) {
            var canvas = document.getElementById("mapCanvas");
            var ctx = canvas.getContext("2d");


            // canvas.width  = window.innerWidth - 24;
            canvas.width = $scope.singleBoxSize * x;
            canvas.height = $scope.singleBoxSize * y;


            $http.get("/colors").success(function (r) {
                $scope.civColor = r;

                // fillRect(x,y,width,height)
                for (var i = 0; i < x; i++) {
                    for (var j = 0; j < y; j++) {
                        var val = mapMatrix[i][j];
                        ctx.fillStyle = val > 15 ? "#ff00ff" : $scope.colorMap[val].color; //fallback if value is strange
                        if ($scope.politicalMode) {
                            if (ctx.fillStyle.toLowerCase() !== "#0f00e6")
                                ctx.fillStyle = "#889091";
                        }
                        ctx.fillRect(i * $scope.singleBoxSize, j * $scope.singleBoxSize, $scope.singleBoxSize, $scope.singleBoxSize);
                    }
                }

            });
        };


        $scope.openSimCreateModal = function () {
            $http.get("/getWorlds").success(function (r) {
                $scope.worlds = r;
                $('select').material_select();
            });
            $('#createSimModal').modal('open');
        };

        $scope.createSimulation = function () {
            console.log($scope.create);
            $http.post("/createSimulation", $scope.create ? $scope.create : {}).success(function (r) {
                console.log("Ok, created.");
                $('#createSimModal').modal('close');
            }).error(function (r) {
                console.error(r);
            });
        };


        $scope.openSimModal = function (id) {
            if ($scope.simFramesInterval)
                $interval.cancel($scope.simFramesInterval);
            $http.get("/getSimulationDetails", {params: {id: id}}).success(function (r) {
                $scope.frames = new Map();
                $scope.provincesMap = new Map();
                $scope.opened = r.simPrev;
                $scope.yScale = 60;
                $scope.opened.width = 100 * r.simPrev.currentFrame / r.simPrev.frameCount;
                // $scope.simProgress='{width: '+width+'%}';
                $('#simModal').modal('open');

                $scope.frameSel = 1;

                if (!r.completed) {
                    $scope.simFramesInterval = $interval(function () {
                        $http.get("/getSimulationDetails", {params: {id: id}}).success(function (r) {
                            if (r.simPrev.completed)
                                $interval.cancel($scope.simFramesInterval);
                            $scope.opened.completed = r.simPrev.completed;
                            $scope.opened.currentFrame = r.simPrev.currentFrame;
                            $scope.opened.width = 100 * r.simPrev.currentFrame / r.simPrev.frameCount;
                        })
                    }, 1000);
                }

                $http.get("/viewWorld", {params: {id: r.simPrev.mapID}}).success(function (r) {
                    $scope.opened.map = r;
                    $scope.generateMap(r.worldMap, r.x, r.y);
                    $scope.goToFrame(1);
                });
            })
        };

        $scope.deleteSimulation = function (id) {
            $http.post("/removeSimulation", id);
        };

        $scope.colorMap = new Map();
        $scope.civMap = undefined;

        $scope.civColorMap = new Map();

        $scope.clearQueue = function () {
            $http.post("/clearQueue");
        };

        $scope.renderFrame = function (frame) {
            // $scope.generateMap($scope.opened.map.worldMap,
            //     $scope.opened.map.x,
            //     $scope.opened.map.y);
            $scope.civMap = undefined;
            var canvas = document.getElementById("simCanvas");
            var ctx = canvas.getContext("2d");

            canvas.width = $scope.singleBoxSize * $scope.opened.map.x + 400;
            canvas.height = $scope.singleBoxSize * $scope.opened.map.y + 200;

            ctx.clearRect(0, 0, canvas.width, canvas.height);

            var valInterval = $scope.singleBoxSize / 5;
            var innerInterval = valInterval + 1;


            $scope.civColorMap = new Map();
            var civIndexMap = new Map();

            for (var i = 0; i < frame.civilizations.length; i++) {
                $scope.civColorMap.set(frame.civilizations[i].id, frame.civilizations[i].colorID);
                frame.civilizations[i].size = 0;
                civIndexMap.set(frame.civilizations[i].id, i);
            }


            for (var i = 0; i < frame.provinces.length; i++) {
                var provinceThird = frame.provinces[i];

                frame.civilizations[civIndexMap.get(provinceThird[2])].size++;

                // 0 - x, 1 - y, 2 - civ
                //paint it black
                ctx.fillStyle = "#000000";
                ctx.fillRect(provinceThird[0] * $scope.singleBoxSize + valInterval, provinceThird[1] * $scope.singleBoxSize + valInterval,
                    $scope.singleBoxSize - valInterval * 2, $scope.singleBoxSize - valInterval * 2);

                if ($scope.singleBoxSize < 5)
                    innerInterval = valInterval;
                if ($scope.politicalMode)
                    innerInterval = 0;
                //fill
                ctx.fillStyle = $scope.civColor[$scope.civColorMap.get(provinceThird[2])];
                ctx.fillRect(provinceThird[0] * $scope.singleBoxSize + innerInterval, provinceThird[1] * $scope.singleBoxSize + innerInterval,
                    $scope.singleBoxSize - innerInterval * 2, $scope.singleBoxSize - innerInterval * 2);
            }


            return frame;
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

        $scope.provinceDetails = false;

        $scope.yScale = 60;

        $scope.isFieldOccupied = function (x, y, frame) {
            if (!frame) return false;
            for (var i = 0, len = frame.provinces.length; i < len; i++) {
                if (frame.provinces[i][0] === x && frame.provinces[i][1] === y) {
                    return true;
                }
            }
            return false;
        };

        $scope.populationMap = function () {
            $scope.civMap = undefined;
            var canvas = document.getElementById("simCanvas");
            var ctx = canvas.getContext("2d");

            canvas.width = $scope.singleBoxSize * $scope.opened.map.x + 400;
            canvas.height = $scope.singleBoxSize * $scope.opened.map.y + 200;

            ctx.clearRect(0, 0, canvas.width, canvas.height);

            var valInterval = $scope.singleBoxSize / 5;
            var innerInterval = valInterval + 1;


            $scope.civColorMap = new Map();
            var civIndexMap = new Map();

            for (var i = 0; i < frame.civilizations.length; i++) {
                $scope.civColorMap.set(frame.civilizations[i].id, frame.civilizations[i].colorID);
                frame.civilizations[i].size = 0;
                civIndexMap.set(frame.civilizations[i].id, i);
            }


            for (var i = 0; i < frame.provinces.length; i++) {
                var provinceThird = frame.provinces[i];

                frame.civilizations[civIndexMap.get(provinceThird[2])].size++;

                // 0 - x, 1 - y, 2 - civ
                //paint it black
                // ctx.fillStyle = "#000000";
                // ctx.fillRect(provinceThird[0] * $scope.singleBoxSize + valInterval, provinceThird[1] * $scope.singleBoxSize + valInterval,
                //     $scope.singleBoxSize - valInterval * 2, $scope.singleBoxSize - valInterval * 2);

                innerInterval = valInterval - 1;
                //fill

                ctx.fillStyle = $scope.civColor[$scope.civColorMap.get(provinceThird[2])];
                ctx.fillRect(provinceThird[0] * $scope.singleBoxSize + innerInterval, provinceThird[1] * $scope.singleBoxSize + innerInterval,
                    $scope.singleBoxSize - innerInterval * 2, $scope.singleBoxSize - innerInterval * 2);
            }

            return frame;
        };


        var whoOccupiesTheField = function (x, y) {
            for (var j = 0; j < $scope.currentFrame.provinces.length; j++) {
                var currentField = $scope.currentFrame.provinces[j];
                if (currentField[0] !== x || currentField[1] !== y)
                    continue;
                var currentCiv = currentField[2];
                if (!$scope.civMap) {
                    $scope.civMap = new Map();
                    for (var i = 0; i < $scope.currentFrame.civilizations.length; i++) {
                        $scope.civMap.set($scope.currentFrame.civilizations[i].id, $scope.currentFrame.civilizations[i].name);
                    }
                }
                return $scope.civMap.get(currentCiv);

            }
        };

        var setScopeOnMouseMovement = function (mousePos) {
            $scope.mouseOverCanvas = true;

            var prevX = $scope.selX;
            var prevY = $scope.selY;

            var mapX = $scope.opened.map.x * $scope.singleBoxSize;
            var mapY = $scope.opened.map.y * $scope.singleBoxSize;


            $scope.selX = Math.floor(mousePos.x / $scope.singleBoxSize);
            $scope.selY = Math.floor(mousePos.y / $scope.singleBoxSize);

            if ($scope.selX >= $scope.opened.map.x || $scope.selY >= $scope.opened.map.y) {
                $scope.mouseOverCanvas = false;
                return;
            }

            $scope.selColor = $scope.colorMap[$scope.opened.map.worldMap[$scope.selX][$scope.selY]].name;

            if (prevX !== $scope.selX || prevY !== $scope.selY) {
                $scope.fieldOccupier = undefined;
                if ($scope.isFieldOccupied($scope.selX, $scope.selY, $scope.currentFrame)) {
                    $scope.fieldOccupier = whoOccupiesTheField($scope.selX, $scope.selY);
                    if ($scope.provincesMap.has('[' + $scope.selX + ', ' + $scope.selY + ']')) {
                        console.log("Got province details from cache")
                        $scope.provinceDetails = $scope.provincesMap.get('[' + $scope.selX + ', ' + $scope.selY + ']');
                    } else {
                        var getX = $scope.selX;
                        var getY = $scope.selY;

                        $scope.provinceDetails = [];

                        $timeout(function () {
                            if (getX !== $scope.selX || getY !== $scope.selY)
                                return;

                            $scope.provinceDetails = [];
                            $scope.provinceDetails.properties = [{name: "Loading...", value: ""}];
                            $http.get("/provinceDetails",
                                {params: {id: $scope.currentFrameDBID, x: $scope.selX, y: $scope.selY}})
                                .success(function (r) {
                                    if (getX !== $scope.selX || getY !== $scope.selY)
                                        return;
                                    $scope.provinceDetails = r;
                                    $scope.provincesMap.set('[' + $scope.selX + ', ' + $scope.selY + ']', r);
                                    var newY = 48 + r.properties.length * 12;
                                    if (newY > $scope.yScale) {
                                        $scope.yScale = newY;
                                    }
                                });
                        }, 200);

                    }
                } else {
                    $scope.provinceDetails = [];
                }

                $scope.detailsX = mousePos.x + $scope.singleBoxSize > mapX - 200 ? mousePos.x - 200 : mousePos.x + $scope.singleBoxSize;
                $scope.detailsY = mousePos.y + $scope.singleBoxSize > mapY - $scope.yScale ? mousePos.y - $scope.yScale : mousePos.y + $scope.singleBoxSize;

            }
        };

        $scope.mouseMove = function (event) {
            var canvas = document.getElementById("mapCanvas");
            var mousePos = getMousePos(canvas, event);
            setScopeOnMouseMovement(mousePos);
        };

        $scope.mouseLeave = function (event) {
            $scope.mouseOverCanvas = false;
        };

        $scope.goToFrame = function (frameNum) {
            $scope.provincesMap = new Map();

            if ($scope.frames.has(frameNum)) {
                var frame = $scope.frames.get(frameNum);
                $scope.currentFrameDBID = frame.frameID;
                $scope.currentFrame = frame;

                $http.get("/getPropertyForFrame",
                    {params: {id: $scope.currentFrameDBID, propertyName: 'population'}})
                    .success(function (r) {
                        $scope.provincesMap = new Map();
                        $.map(r, function (value, index) {
                            $scope.provincesMap.set(index, {properties: [{'name': 'population', 'value': value}]})
                        });
                    });

                return $scope.renderFrame(frame);
            }
            var currFrame = $scope.currentFrame;
            $http.get("/colors").success(function (r) {
                $scope.civColor = r;

                $http.get("/getSimulationFrame",
                    {params: {id: $scope.opened.id, frameNumber: frameNum}})
                    .success(function (r) {
                        if (!$scope.autoPlay && currFrame !== $scope.currentFrame)
                            return;
                        $scope.currentFrame = r;
                        $scope.frames.set(frameNum, r);
                        $scope.currentFrameDBID = r.frameID;
                        $http.get("/getPropertyForFrame",
                            {params: {id: $scope.currentFrameDBID, propertyName: 'population'}})
                            .success(function (r) {
                                $scope.provincesMap = new Map();
                                $.map(r, function (value, index) {
                                    $scope.provincesMap.set(index, {
                                        properties: [{
                                            'name': 'population',
                                            'value': value
                                        }]
                                    })
                                });
                            });
                        return $scope.renderFrame(r);
                    })
            });
        };

        $scope.autoPlay = undefined;
        $scope.playTempo = 3;

        $scope.startPlay = function () {
            if ($scope.autoPlay) return;

            if ($scope.frameSel === $scope.opened.currentFrame)
                $scope.frameSel = 1;

            $scope.autoPlay = $interval(function () {
                if ($scope.frameSel === $scope.opened.currentFrame) {
                    $scope.stopPlay();
                } else {
                    $scope.frameSel++;
                    $scope.goToFrame($scope.frameSel);
                }
            }, 1000 / $scope.playTempo)
        };

        $scope.stopPlay = function () {
            if ($scope.autoPlay) {
                $interval.cancel($scope.autoPlay);
                $scope.autoPlay = undefined;
            }
        };

        $scope.stopRefresh = function () {
            if ($scope.simRefresh) {
                $interval.cancel($scope.simRefresh);
                $scope.simRefresh = undefined;
            }
        };

        $scope.$on('$destroy', function () {
            $scope.stopPlay();
            $scope.stopRefresh();
        });

    }]);