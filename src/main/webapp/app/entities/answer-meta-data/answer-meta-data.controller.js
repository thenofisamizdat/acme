(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('AnswerMetaDataController', AnswerMetaDataController);

    AnswerMetaDataController.$inject = ['AnswerMetaData', 'AnswerMetaDataSearch'];

    function AnswerMetaDataController(AnswerMetaData, AnswerMetaDataSearch) {

        var vm = this;

        vm.answerMetaData = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            AnswerMetaData.query(function(result) {
                vm.answerMetaData = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            AnswerMetaDataSearch.query({query: vm.searchQuery}, function(result) {
                vm.answerMetaData = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
