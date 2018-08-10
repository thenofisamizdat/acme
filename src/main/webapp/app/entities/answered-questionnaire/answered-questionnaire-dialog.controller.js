(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('AnsweredQuestionnaireDialogController', AnsweredQuestionnaireDialogController);

    AnsweredQuestionnaireDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'AnsweredQuestionnaire', 'Answer'];

    function AnsweredQuestionnaireDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, AnsweredQuestionnaire, Answer) {
        var vm = this;

        vm.answeredQuestionnaire = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.answers = Answer.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.answeredQuestionnaire.id !== null) {
                AnsweredQuestionnaire.update(vm.answeredQuestionnaire, onSaveSuccess, onSaveError);
            } else {
                AnsweredQuestionnaire.save(vm.answeredQuestionnaire, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('acmeApp:answeredQuestionnaireUpdate', result);
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
