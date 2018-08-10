(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('QuestionTypeDialogController', QuestionTypeDialogController);

    QuestionTypeDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'QuestionType', 'Question'];

    function QuestionTypeDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, QuestionType, Question) {
        var vm = this;

        vm.questionType = entity;
        vm.clear = clear;
        vm.save = save;
        vm.questions = Question.query();

        if (!vm.questionType.type) vm.questionType.type = "";

        vm.typeCorrect = typeCorrect;

        function typeCorrect(){
            return vm.questionType.type == "Number" || vm.questionType.type == "Text" || vm.questionType.type == "Date" || vm.questionType.type == "Radio" || vm.questionType.type == "Drop Down";
        }

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.questionType.id !== null) {
                QuestionType.update(vm.questionType, onSaveSuccess, onSaveError);
            } else {
                QuestionType.save(vm.questionType, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('acmeApp:questionTypeUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
