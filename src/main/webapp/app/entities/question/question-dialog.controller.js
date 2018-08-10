(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('QuestionDialogController', QuestionDialogController);

    QuestionDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Question', 'QuestionType', 'Questionnaire'];

    function QuestionDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Question, QuestionType, Questionnaire) {
        var vm = this;

        vm.question = entity;
        vm.clear = clear;
        vm.save = save;
        vm.questiontypes = QuestionType.query();

        vm.addToAnswers = addToAnswers;
        if (!vm.question.answers) vm.question.answers = [];
        if (!vm.question.questionType) vm.question.questionType = "";
        vm.answer = "";

        vm.checkQuestionType = checkQuestionType;

        function checkQuestionType(){
            return vm.question.questionType.type == "Drop Down" || vm.question.questionType.type == "Radio";
        }

        function addToAnswers(){
            console.log("nter")
            vm.question.answers.push(vm.answer);
            vm.answer = "";
        }

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.question.id !== null) {
                Question.update(vm.question, onSaveSuccess, onSaveError);
            } else {
                Question.save(vm.question, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('acmeApp:questionUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
