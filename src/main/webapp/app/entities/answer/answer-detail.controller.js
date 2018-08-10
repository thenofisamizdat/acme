(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('AnswerDetailController', AnswerDetailController);

    AnswerDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Answer', 'AnsweredQuestionnaire'];

    function AnswerDetailController($scope, $rootScope, $stateParams, previousState, entity, Answer, AnsweredQuestionnaire) {
        var vm = this;

        vm.answer = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('acmeApp:answerUpdate', function(event, result) {
            vm.answer = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
