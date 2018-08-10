(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('AnswerDialogController', AnswerDialogController);

    AnswerDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Answer', 'AnsweredQuestionnaire'];

    function AnswerDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Answer, AnsweredQuestionnaire) {
        var vm = this;

        vm.answer = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.answeredquestionnaires = AnsweredQuestionnaire.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.answer.id !== null) {
                Answer.update(vm.answer, onSaveSuccess, onSaveError);
            } else {
                Answer.save(vm.answer, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('acmeApp:answerUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.answeredDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
