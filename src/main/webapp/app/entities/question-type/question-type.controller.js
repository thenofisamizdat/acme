(function() {
    'use strict';

    angular
        .module('acmeApp')
        .controller('QuestionTypeController', QuestionTypeController);

    QuestionTypeController.$inject = ['QuestionType', 'QuestionTypeSearch'];

    function QuestionTypeController(QuestionType, QuestionTypeSearch) {

        var vm = this;

        vm.questionTypes = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            QuestionType.query(function(result) {
                vm.questionTypes = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            QuestionTypeSearch.query({query: vm.searchQuery}, function(result) {
                vm.questionTypes = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
