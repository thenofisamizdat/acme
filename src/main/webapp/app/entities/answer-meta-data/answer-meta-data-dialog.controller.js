(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('AnswerMetaDataDialogController', AnswerMetaDataDialogController);

    AnswerMetaDataDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'AnswerMetaData'];

    function AnswerMetaDataDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, AnswerMetaData) {
        var vm = this;

        vm.answerMetaData = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.answerMetaData.id !== null) {
                AnswerMetaData.update(vm.answerMetaData, onSaveSuccess, onSaveError);
            } else {
                AnswerMetaData.save(vm.answerMetaData, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('acmeApp:answerMetaDataUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
