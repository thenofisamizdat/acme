(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('AnswerMetaDataDetailController', AnswerMetaDataDetailController);

    AnswerMetaDataDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'AnswerMetaData'];

    function AnswerMetaDataDetailController($scope, $rootScope, $stateParams, previousState, entity, AnswerMetaData) {
        var vm = this;

        vm.answerMetaData = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('acmeApp:answerMetaDataUpdate', function(event, result) {
            vm.answerMetaData = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
